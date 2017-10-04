package com.example.mac.mismapas1;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * Created by Mac on 11/02/2017.
 */

public class MusicaFondo extends Service {
    private static final String TAG = null;
    MediaPlayer player;

    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.alabama_song);
        player.setLooping(true); // Set looping
        player.setVolume(60,60);
        player.start();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onStart() {
        // TO DO
    }

    public IBinder onUnBind(Intent arg0) {
        player.stop();
        player.release();
        return null;
    }

    public void onStop() {

    }

    public void onPause() {

    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }
}