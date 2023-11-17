package ru.bog5651.waittimer;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ru.bog5651.waittimer.Fragments.PasswordFragment;
import ru.bog5651.waittimer.Fragments.QcCodeFragment;
import ru.bog5651.waittimer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        frameLayout = binding.fragmentRoot;

        MainApplication app = (MainApplication) getApplication();
        TgClient client = app.getClient();
        client.setOnLinkReceived(new TgClient.Receiver() {
            @Override
            public void onReceivedLink(String link) {
                setFragment(new QcCodeFragment());
            }

            @Override
            public void onWaitPassword() {
                setFragment(new PasswordFragment());
            }
        });

        client.init();
    }

    public void setFragment(Fragment fragment) {
        //TODo repace, not add
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .add(frameLayout.getId(), fragment, fragment.getClass().getSimpleName())
                .commitAllowingStateLoss();
    }
}