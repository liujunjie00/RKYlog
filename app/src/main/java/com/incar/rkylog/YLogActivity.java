package com.incar.rkylog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.incar.rkylog.service.YlogService;


public class YLogActivity extends Activity implements View.OnClickListener {
    private Button reset_log,export_log,start_log;
    public static final String SETTINGS_YLOG_VALUE = "YLOG_VALUE";
    private String DATA_LOG_PATH;
    private TextView textView ;
    public final static String  YLOG_START = "log_start";
    public final static String  YLOG_OFF = "log_off";
    public final static String  YLOG_CLEAR = "log_clean";
    public final static String  YLOG_EXPORT = "log_export";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ylog);
        initView();
        initDate();


    }

    /**
     * 初始化状态
     * */
    private void initDate() {

        if (checkYlogStatus()){
            start_log.setText("停止");
        }else {
            start_log.setText("启动");
        }
        String packageName = getApplication().getPackageName();
        DATA_LOG_PATH = "/data/data/"+packageName+"/ylog";
        Log.d("YLogActivity", "initDate: "+DATA_LOG_PATH);
        textView.setText(DATA_LOG_PATH);

    }
    private boolean checkYlogStatus(){

        return !(Settings.System.getInt(getContentResolver(),SETTINGS_YLOG_VALUE,-1) == -1);

    }

    /**
     * */
    private void initView() {
        reset_log = findViewById(R.id.reset_log);
        export_log = findViewById(R.id.export_log);
        start_log = findViewById(R.id.start_log);
        textView = findViewById(R.id.data_path);

        reset_log.setOnClickListener(this);
        export_log.setOnClickListener(this);
        start_log.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int view = v.getId();
        if (view == R.id.reset_log){
            resetLog();

        }else if (view == R.id.export_log){
            exportLog();

        }else if (view == R.id.start_log){
            if (checkYlogStatus()){
                stopLog();
            }else {
                startLog();
            }


        }
    }


    /**
     * 开始log
     * */
    private void startLog() {
        Intent intent=new Intent(this, YlogService.class);
        intent.setAction(YLOG_START);
        startService(intent);//启动service
        start_log.setText("停止");

    }


    /**
     * 导出log*/
    private void exportLog() {
        Intent intent=new Intent(this, YlogService.class);
        intent.setAction(YLOG_EXPORT);
        startService(intent);//启动service

    }
    /**
     * 重置log
     * */
    private void resetLog() {
        Intent intent=new Intent(this, YlogService.class);
        intent.setAction(YLOG_CLEAR);
        startService(intent);//启动service
    }

    private void stopLog() {
        Intent intent=new Intent(this, YlogService.class);
        intent.setAction(YLOG_OFF);
        startService(intent);//启动service
        start_log.setText("启动");

    }
}