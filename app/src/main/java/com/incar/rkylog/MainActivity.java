package com.incar.rkylog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.incar.rkylog.view.WiredChargingAnimation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WiredChargingAnimation.makeWiredChargingAnimation(this, null,
                80, false).show();

    }
}