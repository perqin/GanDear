package com.perqin.gandear;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;

public class TopActivityWatchingService extends Service implements CurrentActivityTimerTask.OnTargetPackageListener {
    private static final String PACKAGE_NAME_ONMYOJI = "com.netease.onmyoji";
    private Timer mCurrentActivityTimer;

    public TopActivityWatchingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mCurrentActivityTimer = new Timer();
        mCurrentActivityTimer.scheduleAtFixedRate(new CurrentActivityTimerTask(this, this, PACKAGE_NAME_ONMYOJI), 0, 500);
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
        FloatingWindowService.startService(this);
    }

    @Override
    public void onPackageBackground() {
        FloatingWindowService.stopService(this);
    }
}
