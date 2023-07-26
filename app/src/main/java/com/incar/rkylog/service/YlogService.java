package com.incar.rkylog.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import com.incar.rkylog.util.SystemUtil;

public class YlogService extends Service {
    private boolean is_log_ing = false;
    private String TAG = "YlogService";
    public final static String  YLOG_START = "log_start";
    public final static String  YLOG_OFF = "log_off";
    public final static String  YLOG_CLEAR = "log_clean";
    public final static String  YLOG_EXPORT = "log_export";
    private static String  YLOG_DATE_PATH = "/data/data/com.example.myapplication/ylog";
    public final static String  YLOG_SD_PATH = "/storage/emulated/0/ylog";
    public static final String SETTINGS_YLOG_VALUE = "YLOG_VALUE";
    public YlogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: YlogService ");
        initDate();
    }

    private boolean checkYlogStatus(){

        return !(Settings.System.getInt(getContentResolver(),SETTINGS_YLOG_VALUE,-1) == -1);

    }

    private boolean setYlogStatus(int status){

        return Settings.System.putInt(getContentResolver(),SETTINGS_YLOG_VALUE,status);

    }

    /**
     * 初始化 数据
     * */
    private void initDate() {
        String packageName = getApplication().getPackageName();
        YLOG_DATE_PATH = "/data/data/"+packageName+"/ylog";
        if (checkYlogStatus()){
            ylogStart();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!is_log_ing){
                SystemUtil.execShellCmd("whoami ");
                SystemUtil.execShellCmd("touch "+YLOG_DATE_PATH);
                SystemUtil.execShellCmd("logcat -f "+YLOG_DATE_PATH);
            }

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return super.onStartCommand(null, flags, startId);
        String action = intent.getAction();
        if (action == null) return super.onStartCommand(null, flags, startId);
        switch (action){
            case YLOG_START:
                ylogStart();
                break;
            case YLOG_CLEAR:
                ylogClean();
                break;
            case YLOG_OFF:
                ylogOFF();
                break;
            case YLOG_EXPORT:
                ylogExport();
                break;

        }
        return super.onStartCommand(intent, flags, startId);
    }
    private Thread thread;

    private void ylogStart() {
        Log.d(TAG, "ylogStart: ylog 启动 ");
        thread = new Thread(runnable);
        thread.start();
        setYlogStatus(1);
    }

    private void ylogClean() {
        ylogOFF();
        SystemUtil.execShellCmd("rm "+YLOG_DATE_PATH);
        SystemUtil.execShellCmd("rm "+YLOG_SD_PATH);
        SystemUtil.execShellCmd("logcat -c ");

        SystemUtil.execShellCmd("touch "+YLOG_DATE_PATH);
    }

    /**
     * 关闭 Log线程
     * */
    private void ylogOFF() {
        String logcatList = SystemUtil.execShellCmd("ps -U system ");
        String[] pid_list  = logcatList.split("\n");
        for (int i = 0; i < pid_list.length; i++) {
            String info = pid_list[i];
            if (info.contains("logcat")){
                String s_temp = info.substring(6,info.length());
                s_temp = s_temp.trim();
                String value = s_temp.substring(0,s_temp.indexOf(" "));
                SystemUtil.execShellCmd("kill "+value);
            }
        }
        setYlogStatus(-1);

    }

    /**
     * 导出log
     * */
    private void ylogExport() {
        Log.d(TAG, "ylogExport: log导出");
        SystemUtil.execShellCmd("cp "+YLOG_DATE_PATH +" "+YLOG_SD_PATH);
        
    }
}