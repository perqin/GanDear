package com.perqin.gandear;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FloatingWindowService extends Service {
    private static final String TAG = "FloatingWindowService";

    public FloatingWindowService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
