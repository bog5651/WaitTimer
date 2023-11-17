package ru.bog5651.waittimer;

import android.app.Application;

public class MainApplication extends Application {
    private TgClient client;

    @Override
    public void onCreate() {
        super.onCreate();

        client = new TgClient(getCacheDir());
    }

    public TgClient getClient() {
        return this.client;
    }
}
