package com.thebeginner.volumiovibes.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.thebeginner.volumiovibes.R;

public class SplashActivity extends Activity {
    private static final int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_activity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, SetupMyVolumioActivity.class));
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
