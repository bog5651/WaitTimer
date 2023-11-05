package ru.bog5651.waittimer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ru.bog5651.waittimer.databinding.ActivityMainBinding;

public class MainActivity extends Activity {
    private TextView timerView;
    private View root;
    private ActivityMainBinding binding;
    final Handler h = new Handler();

    private int seconds = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        timerView = binding.timer;
        root = binding.root;

        root.setOnClickListener(v -> {
            seconds = 30;
            timerView.setText(String.valueOf(seconds));
        });

        h.postDelayed(new TimerTask() {
            @Override
            public void run() {
                if (seconds <= 0) {
                    finishAffinity();
                    return;
                }
                runOnUiThread(() -> {
                    seconds = seconds - 1;
                    timerView.setText(String.valueOf(seconds));
                });
                h.postDelayed(this, 1000);
            }
        }, 1000);

        timerView.setText(String.valueOf(seconds));
    }
}