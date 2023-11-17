package ru.bog5651.waittimer;

import android.util.Log;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class TgClient {
    private final File appDir;
    private TdApi.AuthorizationState state;
    private Client client;
    public String link;

    private Receiver onLinkReceived;

    public TgClient(File appDir) {
        this.appDir = appDir;
    }

    public void init() {
        client = Client.create(object -> {
            Log.d("My app", String.format("update handler: %s", object.toString()));

            switch (object.getConstructor()) {
                case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
                    onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
                    break;
            }
        }, e -> {
            Log.d("My app", String.format("update handler error %s\n", e.toString()));
        }, e -> {
            Log.d("My app", String.format("def handler error %s\n", e.toString()));
        });

        this.setTdlibParameters(null);
    }

    public void setOnLinkReceived(Receiver onLinkReceived) {
        this.onLinkReceived = onLinkReceived;
    }

    private void onAuthorizationStateUpdated(TdApi.AuthorizationState authorizationState) {
        if (authorizationState != null) {
            state = authorizationState;
        }
        switch (state.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                this.setTdlibParameters((TdApi.AuthorizationStateWaitTdlibParameters) authorizationState);
                break;
            case TdApi.AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR:
                link = ((TdApi.AuthorizationStateWaitOtherDeviceConfirmation) state).link;
                this.onLinkReceived.onReceivedLink(link);
                break;
            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                this.checkDBEncriptionKey();
                break;
            case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
                break;
            case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
                break;
            case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
                break;
            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                break;
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
                break;
            case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR:
                this.onLinkReceived.onWaitPassword();
                break;
            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR:
                this.switchToOtherDeviseConfirmation();
                break;
            case TdApi.AuthorizationStateWaitRegistration.CONSTRUCTOR:
                break;
        }
    }

    private void switchToOtherDeviseConfirmation() {
        TdApi.RequestQrCodeAuthentication request = new TdApi.RequestQrCodeAuthentication();
        client.send(request, null);
    }

    private void setTdlibParameters(TdApi.AuthorizationStateWaitTdlibParameters authorizationStateWaitTdlibParameters) {
        TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
        TdApi.SetTdlibParameters request = new TdApi.SetTdlibParameters(parameters);
        parameters.databaseDirectory = appDir.getAbsolutePath() + "/tdlib/db";
        parameters.useTestDc = false;
        parameters.useMessageDatabase = false;
        parameters.useSecretChats = false;
        parameters.useFileDatabase = true;
        parameters.apiId = 1;
        parameters.apiHash = "none";
        parameters.systemLanguageCode = "ru";
        parameters.deviceModel = "WearOs";
        parameters.applicationVersion = "1.0";
        parameters.enableStorageOptimizer = true;

        client.send(request, e -> {
            Log.d("My app", String.format("setTdlibParameters handler error %s\n", e.toString()));
        });
    }

    private void checkDBEncriptionKey() {
        String key = "mysuperenckey";
        byte[] data = null;
        try {
            data = key.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        TdApi.CheckDatabaseEncryptionKey request = new TdApi.CheckDatabaseEncryptionKey(data);
        client.send(request, null);
    }

    public interface Receiver {
        void onReceivedLink(String link);
        void onWaitPassword();
    }
}
