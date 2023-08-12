package com.incar.rkylog.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.incar.rkylog.service.ChargingAnimationService;
import com.incar.rkylog.service.YlogService;

public class BootBr extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = new Intent(context,YlogService.class);
        context.startService(intent2);
        Log.d("ylog", "onReceive:start  ");
        /*Intent intent3 = new Intent(context, ChargingAnimationService.class);
        context.startService(intent3);*/

    }

}
