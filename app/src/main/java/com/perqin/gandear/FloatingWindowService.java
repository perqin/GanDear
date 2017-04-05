package com.perqin.gandear;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

public class FloatingWindowService extends Service {
    private static final String TAG = "FloatingWindowService";

    private TextView textView;
    private WindowManager windowManager;

    public FloatingWindowService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        textView = new TextView(this);
        textView.setBackgroundColor(Color.RED);
        textView.setText(R.string.app_name);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        lp.gravity = Gravity.TOP | Gravity.LEFT;
        lp.x = 100;
        lp.y = 100;
        windowManager.addView(textView, lp);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textView != null) {
            windowManager.removeView(textView);
            textView = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
