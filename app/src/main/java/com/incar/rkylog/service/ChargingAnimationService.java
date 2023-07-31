package com.incar.rkylog.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;

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
                int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                WiredChargingAnimation.makeWiredChargingAnimation(context, null,
                        batteryLevel, false).show();

            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * 监听广播
     */
    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter();
        // 第三步，设置频道(即表明要监听什么广播
        //intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        //intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(batteryBroadcastReceiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryBroadcastReceiver);
    }
}