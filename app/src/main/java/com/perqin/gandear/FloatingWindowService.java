package com.perqin.gandear;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

public class FloatingWindowService extends Service {
    private static final String TAG = "FloatingWindowService";

    private WindowManager mWindowManager;
    private boolean mToggleOpened;

    private View mFloatingWindowView;
    private ImageButton mToggleButton;
    private RecyclerView mGoalsRecyclerView;
    private RecyclerView mGoalDetailsRecyclerView;

    public FloatingWindowService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mToggleOpened = false;
        addFloatingWindowView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeFloatingWindowView();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void addFloatingWindowView() {
        mFloatingWindowView = LayoutInflater.from(this).inflate(R.layout.layout_floating_window, null, false);
        mToggleButton = (ImageButton) mFloatingWindowView.findViewById(R.id.toggle_button);
        mToggleButton.setOnClickListener(new OnToggleButtonClickListener());
        mGoalsRecyclerView = (RecyclerView) mFloatingWindowView.findViewById(R.id.goals_recycler_view);
        mGoalDetailsRecyclerView = (RecyclerView) mFloatingWindowView.findViewById(R.id.goal_details_recycler_view);

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
        mWindowManager.addView(mFloatingWindowView, lp);
    }

    private void removeFloatingWindowView() {
        if (mFloatingWindowView != null) {
            mWindowManager.removeView(mFloatingWindowView);
            mFloatingWindowView = null;
        }
    }

    private class OnToggleButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mToggleOpened = !mToggleOpened;
            final WindowManager.LayoutParams lp;
            if (mToggleOpened) {
                mGoalsRecyclerView.setVisibility(View.VISIBLE);
                mGoalDetailsRecyclerView.setVisibility(View.VISIBLE);
                lp = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        400,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT
                );
                lp.gravity = Gravity.TOP | Gravity.LEFT;
                lp.x = 100;
                lp.y = 400;
            } else {
                mGoalsRecyclerView.setVisibility(View.GONE);
                mGoalDetailsRecyclerView.setVisibility(View.GONE);
                lp = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT
                );
                lp.gravity = Gravity.TOP | Gravity.LEFT;
                lp.x = 100;
                lp.y = 100;
            }
            mWindowManager.updateViewLayout(mFloatingWindowView, lp);
        }
    }
}
