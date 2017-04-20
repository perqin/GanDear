package com.perqin.gandear.topactivity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.perqin.gandear.R;
import com.perqin.gandear.floatingwindow.FloatingWindowServiceHelper;

import java.util.Timer;

public class TopActivityWatchingService extends Service implements TopActivityTimerTask.OnTargetPackageListener {
    private Timer mCurrentActivityTimer;

    public TopActivityWatchingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mCurrentActivityTimer = new Timer();
        mCurrentActivityTimer.scheduleAtFixedRate(new TopActivityTimerTask(this, this, TopActivityServiceHelper.PACKAGE_NAME_ONMYOJI), 0, 500);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mCurrentActivityTimer.cancel();
        mCurrentActivityTimer = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPackageForeground() {
        if (FloatingWindowServiceHelper.canStartService(this)) {
            if (!FloatingWindowServiceHelper.isServiceRunning(this)) {
                FloatingWindowServiceHelper.startService(this);
            }
        } else {
            Toast.makeText(this, R.string.to_show_floating_window_when_you_are_playing_the_game_on_android_lower_than_kitkat_we_need_draw_over_apps_permission_granted, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPackageBackground() {
        if (FloatingWindowServiceHelper.isServiceRunning(this)) {
            FloatingWindowServiceHelper.stopService(this);
        }
    }
}
