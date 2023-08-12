package com.incar.rkylog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import com.incar.rkylog.service.YlogService;

import java.io.File;
import java.io.IOException;


//
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*WiredChargingAnimation.makeWiredChargingAnimation(this, null,
                80, false).show();*/

        /*Intent intent3 = new Intent(this, ChargingAnimationService.class);
        this.startService(intent3);*/

        Intent intent2 = new Intent(this, YlogService.class);
        this.startService(intent2);
        //playSound(true);
    }

    private final MediaPlayer mediaPlayer = new MediaPlayer();
    String path1 = "/product/media/audio/ui/VideoRecord1.ogg";
    String path2 = "/product/media/audio/ui/VideoStop1.ogg";
    private void playSound(boolean start){
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        Log.d("liujunjie44", "playSound: "+currentVolume);
        File file = null;
        if (start){
            // 开始播放
            file = new File(path1);

        }else {
            //暂停播放
            file = new File(path2);

        }
        try {
            mediaPlayer.reset();
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.setAudioAttributes(attrBuilder.build());
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mediaPlayer.start();

    }
}