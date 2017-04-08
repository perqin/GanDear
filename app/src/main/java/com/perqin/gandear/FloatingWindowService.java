package com.perqin.gandear;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class FloatingWindowService extends Service implements GoalRecyclerAdapter.OnGoalClickListener, ShishensRecyclerAdapter.OnItemClickListener {
    private static final String TAG = "FloatingWindowService";
    private static final int STATE_CLOSED = 0;
    private static final int STATE_EXPANDED_INITIAL = 1;
    private static final int STATE_EXPANDED_SELECTING_SHISHEN = 2;
    private static final int STATE_EXPANDED_SHISHEN_PRESENCE = 3;

    private int mState;
    private WindowManager mWindowManager;
    private GoalRecyclerAdapter mGoalRecyclerAdapter;
    private ShishensRecyclerAdapter mShishensRecyclerAdapter;
    private boolean mToggleOpened;

    private View mFloatingWindowView;
    private ImageButton mToggleButton;
    private RecyclerView mGoalsRecyclerView;
    private RecyclerView mGoalDetailsRecyclerView;
    private RecyclerView mShishensRecyclerView;
    private TextView mInputText;
    private TableLayout mTableLayout;

    public FloatingWindowService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mGoalRecyclerAdapter = new GoalRecyclerAdapter(new ArrayList<>(), this);
        mShishensRecyclerAdapter = new ShishensRecyclerAdapter(AppRepository.getInstance(this).getShishens(), this);
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

    @Override
    public void onGoalClick(Shishen shishen) {
        // TODO: Implement onGoalClick
    }

    @Override
    public void onGoalLongClick(Shishen shishen) {
        // TODO: Implement onGoalLongClick
    }

    @Override
    public void onAdderClick() {
        setState(STATE_EXPANDED_SELECTING_SHISHEN);
    }

    private void setState(int state) {
        mState = state;
        switch (mState) {
            case STATE_CLOSED:
                mGoalsRecyclerView.setVisibility(View.GONE);
                mGoalDetailsRecyclerView.setVisibility(View.GONE);
                mShishensRecyclerView.setVisibility(View.GONE);
                mInputText.setVisibility(View.GONE);
                mTableLayout.setVisibility(View.GONE);
                break;
            case STATE_EXPANDED_INITIAL:
                mGoalsRecyclerView.setVisibility(View.VISIBLE);
                mGoalDetailsRecyclerView.setVisibility(View.GONE);
                mShishensRecyclerView.setVisibility(View.GONE);
                mInputText.setVisibility(View.GONE);
                mTableLayout.setVisibility(View.GONE);
                break;
            case STATE_EXPANDED_SELECTING_SHISHEN:
                mGoalsRecyclerView.setVisibility(View.VISIBLE);
                mGoalDetailsRecyclerView.setVisibility(View.GONE);
                mShishensRecyclerView.setVisibility(View.VISIBLE);
                mInputText.setVisibility(View.VISIBLE);
                mTableLayout.setVisibility(View.VISIBLE);
                break;
            case STATE_EXPANDED_SHISHEN_PRESENCE:
                mGoalsRecyclerView.setVisibility(View.VISIBLE);
                mGoalDetailsRecyclerView.setVisibility(View.VISIBLE);
                mShishensRecyclerView.setVisibility(View.GONE);
                mInputText.setVisibility(View.GONE);
                mTableLayout.setVisibility(View.GONE);
            default: break;
        }
    }

    private void addFloatingWindowView() {
        mFloatingWindowView = LayoutInflater.from(this).inflate(R.layout.layout_floating_window, null, false);
        mToggleButton = (ImageButton) mFloatingWindowView.findViewById(R.id.toggle_button);
        mToggleButton.setOnClickListener(new OnToggleButtonClickListener());
        mGoalsRecyclerView = (RecyclerView) mFloatingWindowView.findViewById(R.id.goals_recycler_view);
        mGoalsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mGoalsRecyclerView.setAdapter(mGoalRecyclerAdapter);
        mGoalDetailsRecyclerView = (RecyclerView) mFloatingWindowView.findViewById(R.id.goal_details_recycler_view);
        mShishensRecyclerView = (RecyclerView) mFloatingWindowView.findViewById(R.id.shishens_recycler_view);
        mShishensRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mShishensRecyclerView.setAdapter(mShishensRecyclerAdapter);
        mInputText = (TextView) mFloatingWindowView.findViewById(R.id.input_text);
        mTableLayout = (TableLayout) mFloatingWindowView.findViewById(R.id.table_layout);

        setState(STATE_CLOSED);

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

    private void toggleClosedAndExpanded(boolean expand) {
        mToggleOpened = expand;
        final WindowManager.LayoutParams lp;
        if (mToggleOpened) {
            setState(STATE_EXPANDED_INITIAL);
            lp = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            );
            lp.gravity = Gravity.TOP | Gravity.LEFT;
            lp.x = 0;
            lp.y = 0;
        } else {
            setState(STATE_CLOSED);
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

    @Override
    public void onShishenItemClick(Shishen shishen) {
        boolean added = mGoalRecyclerAdapter.addShishen(shishen);
        if (!added) {
            // TODO: Show tips that the shishen is already added
        } else {
            setState(STATE_EXPANDED_INITIAL);
        }
    }

    private class OnToggleButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            toggleClosedAndExpanded(!mToggleOpened);
        }
    }
}
