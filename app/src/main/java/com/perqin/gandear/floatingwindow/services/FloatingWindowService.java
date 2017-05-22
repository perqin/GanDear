package com.perqin.gandear.floatingwindow.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.perqin.gandear.R;
import com.perqin.gandear.data.AppRepository;
import com.perqin.gandear.data.models.Shishen;
import com.perqin.gandear.floatingwindow.FloatingWindowServiceHelper;
import com.perqin.gandear.floatingwindow.NewScreenshotHelper;
import com.perqin.gandear.floatingwindow.ui.DraggableToggleImageButton;
import com.perqin.gandear.floatingwindow.ui.GoalDetailRecyclerAdapter;
import com.perqin.gandear.floatingwindow.ui.GoalRecyclerAdapter;
import com.perqin.gandear.floatingwindow.ui.QueryHelper;
import com.perqin.gandear.floatingwindow.ui.ShishensRecyclerAdapter;
import com.perqin.gandear.ocr.OcrTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FloatingWindowService extends Service
        implements GoalRecyclerAdapter.OnGoalClickListener, ShishensRecyclerAdapter.OnItemClickListener, View.OnClickListener, QueryHelper.QueryChangeListener, NewScreenshotHelper.OnNewScreenshotListener {
    private static final String TAG = "FloatingWindowService";
    private static final int STATE_CLOSED = 0;
    private static final int STATE_EXPANDED_INITIAL = 1;
    private static final int STATE_EXPANDED_SELECTING_SHISHEN = 2;
    private static final int STATE_EXPANDED_SHISHEN_PRESENCE = 3;

    private int mState;
    private WindowManager mWindowManager;
    private GoalRecyclerAdapter mGoalRecyclerAdapter;
    private GoalDetailRecyclerAdapter mGoalDetailRecyclerAdapter;
    private ShishensRecyclerAdapter mShishensRecyclerAdapter;
    private boolean mToggleOpened;
    private QueryHelper mQueryHelper;

    private View mFloatingWindowView;
    private DraggableToggleImageButton mToggleButton;
    @BindView(R.id.quick_add_button)
    ImageButton mQuickAddButton;
    private RecyclerView mGoalsRecyclerView;
    private RecyclerView mGoalDetailsRecyclerView;
    private RecyclerView mShishensRecyclerView;
    private TableLayout mTableLayout;
    // Widgets for query
    @BindView(R.id.input_text)
    TextView mInputText;
    @BindView(R.id.window_root_layout)
    ConstraintLayout mRootLayout;
    private NewScreenshotHelper mNewScreenshotHelper;

    public FloatingWindowService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mGoalRecyclerAdapter = new GoalRecyclerAdapter(AppRepository.getInstance(this).getGoalShishens(), this);
        mGoalDetailRecyclerAdapter = new GoalDetailRecyclerAdapter();
        mShishensRecyclerAdapter = new ShishensRecyclerAdapter(AppRepository.getInstance(this).getShishens(), this);
        mToggleOpened = false;
        mQueryHelper = new QueryHelper();
        mQueryHelper.setListener(this);
        mNewScreenshotHelper = new NewScreenshotHelper(this, new Handler(Looper.getMainLooper()), this);
        addFloatingWindowView();
        // Check update of data.json
        final AppRepository appRepository = AppRepository.getInstance(this);
        appRepository.checkDataJsonUpdate().subscribe(version -> {
            if (version != -1) {
                appRepository.updateLatestDataJson(version).subscribe(data -> {
                    mShishensRecyclerAdapter.refreshShishens(data.shishens);
                    if (mState == STATE_EXPANDED_SHISHEN_PRESENCE) {
                        setState(STATE_EXPANDED_INITIAL);
                    }
                }, throwable -> {
                    Log.w(TAG, "onCreate: Failed to update", throwable);
                });
            }
        }, throwable -> {
            Log.w(TAG, "onCreate: Failed to get version.json", throwable);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeFloatingWindowView();
        mNewScreenshotHelper.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onGoalClick(Shishen shishen) {
        setState(STATE_EXPANDED_SHISHEN_PRESENCE);
        mGoalDetailRecyclerAdapter.reloadPresence(shishen.getId(), AppRepository.getInstance(this).getShishenPresences(shishen.getId()));
    }

    @Override
    public void onGoalLongClick(Shishen shishen) {
        AppRepository.getInstance(this).removeGoalShishen(shishen);
        setState(STATE_EXPANDED_INITIAL);
        mGoalRecyclerAdapter.removeShishen(shishen);
        Toast.makeText(this, getString(R.string.you_have_removed_shishen_PH, shishen.getName()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdderClick() {
        if (mState == STATE_EXPANDED_SELECTING_SHISHEN) {
            setState(STATE_EXPANDED_INITIAL);
        } else {
            setState(STATE_EXPANDED_SELECTING_SHISHEN);
        }
    }

    @Override
    public void onShishenItemClick(Shishen shishen) {
        boolean added = mGoalRecyclerAdapter.addShishen(shishen);
        if (!added) {
            Toast.makeText(this, R.string.this_shishen_is_already_added, Toast.LENGTH_SHORT).show();
        } else {
            AppRepository.getInstance(this).addGoalShishen(shishen);
            mQueryHelper.clearQuery();
            setState(STATE_EXPANDED_INITIAL);
        }
    }

    @OnClick({ R.id.delete_button, R.id.abc_button, R.id.def_button,
            R.id.ghi_button, R.id.jkl_button, R.id.mno_button,
            R.id.pqrs_button, R.id.tuv_button, R.id.wxyz_button, R.id.quick_add_button })
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_button:
                String q = mQueryHelper.getQuery();
                if (!q.isEmpty()) {
                    mQueryHelper.setQuery(q.substring(0, q.length() - 1));
                }
                break;
            case R.id.abc_button:
                mQueryHelper.appendToQuery("2");
                break;
            case R.id.def_button:
                mQueryHelper.appendToQuery("3");
                break;
            case R.id.ghi_button:
                mQueryHelper.appendToQuery("4");
                break;
            case R.id.jkl_button:
                mQueryHelper.appendToQuery("5");
                break;
            case R.id.mno_button:
                mQueryHelper.appendToQuery("6");
                break;
            case R.id.pqrs_button:
                mQueryHelper.appendToQuery("7");
                break;
            case R.id.tuv_button:
                mQueryHelper.appendToQuery("8");
                break;
            case R.id.wxyz_button:
                mQueryHelper.appendToQuery("9");
                break;
            case R.id.quick_add_button:
                startDetecting();
                break;
            default:
                break;
        }
    }

    @Override
    public void onQueryChange(String oldQuery, String newQuery) {
        mInputText.setText(newQuery);
        if (newQuery.isEmpty()) {
            mShishensRecyclerAdapter.refreshShishens(AppRepository.getInstance(this).getShishens());
        } else {
            mShishensRecyclerAdapter.refreshShishens(AppRepository.getInstance(this).queryShishens(newQuery));
        }
    }

    @Override
    public void onNewScreenshot(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Bitmap[] bitmaps = new Bitmap[3];
        bitmaps[0] = Bitmap.createBitmap(bitmap, 320, 570, 380, 298);
        bitmaps[1] = Bitmap.createBitmap(bitmap, 772, 570, 380, 298);
        bitmaps[2] = Bitmap.createBitmap(bitmap, 1223, 570, 380, 298);
        new OcrTask(this, strings -> {
            for (String s : strings) {
                Log.d(TAG, "onNewScreenshot: " + s);
            }
        }).execute(bitmaps);
    }

    private void setState(int state) {
        mState = state;
        switch (mState) {
            case STATE_CLOSED:
                mQuickAddButton.setVisibility(View.GONE);
                mGoalsRecyclerView.setVisibility(View.GONE);
                mGoalDetailsRecyclerView.setVisibility(View.GONE);
                mShishensRecyclerView.setVisibility(View.GONE);
                mInputText.setVisibility(View.GONE);
                mTableLayout.setVisibility(View.GONE);
                break;
            case STATE_EXPANDED_INITIAL:
//                mQuickAddButton.setVisibility(View.VISIBLE);
                mGoalsRecyclerView.setVisibility(View.VISIBLE);
                mGoalDetailsRecyclerView.setVisibility(View.GONE);
                mShishensRecyclerView.setVisibility(View.GONE);
                mInputText.setVisibility(View.GONE);
                mTableLayout.setVisibility(View.GONE);
                break;
            case STATE_EXPANDED_SELECTING_SHISHEN:
//                mQuickAddButton.setVisibility(View.VISIBLE);
                mGoalsRecyclerView.setVisibility(View.VISIBLE);
                mGoalDetailsRecyclerView.setVisibility(View.GONE);
                mShishensRecyclerView.setVisibility(View.VISIBLE);
                mInputText.setVisibility(View.VISIBLE);
                mTableLayout.setVisibility(View.VISIBLE);
                break;
            case STATE_EXPANDED_SHISHEN_PRESENCE:
//                mQuickAddButton.setVisibility(View.VISIBLE);
                mGoalsRecyclerView.setVisibility(View.VISIBLE);
                mGoalDetailsRecyclerView.setVisibility(View.VISIBLE);
                mShishensRecyclerView.setVisibility(View.GONE);
                mInputText.setVisibility(View.GONE);
                mTableLayout.setVisibility(View.GONE);
            default: break;
        }
    }

    @SuppressLint("RtlHardcoded")
    private void addFloatingWindowView() {
        mFloatingWindowView = LayoutInflater.from(this).inflate(R.layout.layout_floating_window, null, false);
        mToggleButton = (DraggableToggleImageButton) mFloatingWindowView.findViewById(R.id.toggle_button);
        mToggleButton.setOnClickListener(new OnToggleButtonClickListener());
        mToggleButton.setOnDraggableButtonTouchEventListener(new DraggableToggleImageButton.OnDraggableToggleButtonTouchEvent() {
            @Override
            public void onDrag(float dx, float dy) {
                if (mState == STATE_CLOSED) {
                    WindowManager.LayoutParams lp = (WindowManager.LayoutParams) mFloatingWindowView.getLayoutParams();
                    lp.x += dx;
                    lp.y += dy;
                    mWindowManager.updateViewLayout(mFloatingWindowView, lp);
                }
            }

            @Override
            public void onRelease() {
                if (mState == STATE_CLOSED) {
                    WindowManager.LayoutParams lp = (WindowManager.LayoutParams) mFloatingWindowView.getLayoutParams();
                    AppRepository.getInstance(FloatingWindowService.this).saveFloatingWindowPosition(lp.x, lp.y);
                }
            }
        });
        mGoalsRecyclerView = (RecyclerView) mFloatingWindowView.findViewById(R.id.goals_recycler_view);
        mGoalsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mGoalsRecyclerView.setAdapter(mGoalRecyclerAdapter);
        mGoalDetailsRecyclerView = (RecyclerView) mFloatingWindowView.findViewById(R.id.goal_details_recycler_view);
        mGoalDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mGoalDetailsRecyclerView.setAdapter(mGoalDetailRecyclerAdapter);
        mShishensRecyclerView = (RecyclerView) mFloatingWindowView.findViewById(R.id.shishens_recycler_view);
        mShishensRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mShishensRecyclerView.setAdapter(mShishensRecyclerAdapter);
        mTableLayout = (TableLayout) mFloatingWindowView.findViewById(R.id.table_layout);
        ButterKnife.bind(this, mFloatingWindowView);

        setState(STATE_CLOSED);

        int[] pos = new int[2];
        AppRepository.getInstance(this).readFloatingWindowPosition(pos);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                FloatingWindowServiceHelper.getFloatingWindowType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        lp.gravity = Gravity.TOP | Gravity.LEFT;
        lp.x = pos[0];
        lp.y = pos[1];
        mWindowManager.addView(mFloatingWindowView, lp);
    }

    private void removeFloatingWindowView() {
        if (mFloatingWindowView != null) {
            mWindowManager.removeView(mFloatingWindowView);
            mFloatingWindowView = null;
        }
    }

    @SuppressLint("RtlHardcoded")
    private void toggleClosedAndExpanded(boolean expand) {
        mToggleOpened = expand;
        final WindowManager.LayoutParams lp;
        final int padding;
        if (mToggleOpened) {
            padding = (int) (16 * getResources().getDisplayMetrics().density + 0.5f);
            lp = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    FloatingWindowServiceHelper.getFloatingWindowType(),
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            );
            lp.gravity = Gravity.TOP | Gravity.LEFT;
            lp.x = 0;
            lp.y = 0;
            setState(STATE_EXPANDED_INITIAL);
            mToggleButton.setImageResource(R.drawable.ic_close);
        } else {
            int[] pos = new int[2];
            AppRepository.getInstance(this).readFloatingWindowPosition(pos);
            padding = 0;
            lp = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    FloatingWindowServiceHelper.getFloatingWindowType(),
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            );
            lp.gravity = Gravity.TOP | Gravity.LEFT;
            lp.x = pos[0];
            lp.y = pos[1];
            setState(STATE_CLOSED);
            mToggleButton.setImageResource(R.drawable.ic_expand);
        }
        mRootLayout.setPadding(padding, padding, padding, padding);
        mRootLayout.setBackgroundColor(mToggleOpened ? ContextCompat.getColor(this, R.color.colorWindowBackground) : Color.TRANSPARENT);
        mWindowManager.updateViewLayout(mFloatingWindowView, lp);
    }

    private void startDetecting() {
        toggleClosedAndExpanded(false);
        mNewScreenshotHelper.enable();
    }

    private class OnToggleButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            toggleClosedAndExpanded(!mToggleOpened);
        }
    }
}
