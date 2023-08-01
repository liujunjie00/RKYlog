package com.incar.rkylog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.incar.rkylog.service.ChargingAnimationService;
import com.incar.rkylog.view.WiredChargingAnimation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*WiredChargingAnimation.makeWiredChargingAnimation(this, null,
                80, false).show();*/

        Intent intent3 = new Intent(this, ChargingAnimationService.class);
        this.startService(intent3);

    }
}