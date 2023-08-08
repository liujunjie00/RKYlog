package com.incar.rkylog.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.view.View;

import com.incar.rkylog.MainActivity;
import com.incar.rkylog.view.WiredChargingAnimation;


/**
 * 模仿华为的充电动画
 * */
public class ChargingAnimationService extends Service {
    public ChargingAnimationService() {
    }

    private final BroadcastReceiver batteryBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            String action = intent.getAction();
            if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
               // int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                BatteryManager batteryManager = getSystemService(BatteryManager.class);
                int batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                WiredChargingAnimation.makeWiredChargingAnimation(context, null,
                        batteryLevel, false).show();
                /*Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);*/
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    /**
     * 监听广播
     */
    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        //intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(batteryBroadcastReceiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryBroadcastReceiver);
        Intent intent3 = new Intent(this, ChargingAnimationService.class);
        getApplicationContext().startService(intent3);
    }
}