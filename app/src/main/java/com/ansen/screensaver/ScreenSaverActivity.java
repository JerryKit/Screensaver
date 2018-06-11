package com.ansen.screensaver;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class ScreenSaverActivity extends Activity {
    protected static final String TAG = "ScreenSaverActivity";
    private static PowerManager.WakeLock mWakeLock;
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        // lp.userActivityTimeout = USER_ACTIVITY_TIMEOUT_WHEN_NO_PROX_SENSOR;
        getWindow().setAttributes(lp);

        setContentView(R.layout.activity_screen_saver);
        mVideoView = (VideoView) findViewById(R.id.vv);
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "ScreenSaver");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWakeLock.acquire();
        play();
    }

    private void play() {
        SharedPreferences sp = getSharedPreferences(PickVideo.SP_NAME, Context.MODE_PRIVATE);
        String videoPath = sp.getString(PickVideo.SP_Path, null);
        Log.e(TAG, "videoPath:" + videoPath);
        if (videoPath == null) {
            Toast.makeText(this, "Please select one video", Toast.LENGTH_LONG).show();
            return;
        }

        Uri uri = Uri.parse(videoPath);
        MediaController mc = new MediaController(this);
        mc.setVisibility(View.INVISIBLE);
        mVideoView.setMediaController(mc);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setVideoURI(uri);
        //mVideoView.setVideoPath(files[0].getAbsolutePath());
        mVideoView.start();
        mVideoView.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });
        mVideoView.requestFocus();
    }

    @Override
    protected void onPause() {
        mWakeLock.release();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            finish();
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}