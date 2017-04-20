package com.perqin.gandear.topactivity;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;
import java.util.TimerTask;

/**
 * Author   : perqin
 * Date     : 17-4-17
 */

public class TopActivityTimerTask extends TimerTask {
    private Context mContext;
    private OnTargetPackageListener mListener;
    private String mTargetPackage;
    private String mCurrentPackage;

    public TopActivityTimerTask(Context context, OnTargetPackageListener listener, String targetPackage) {
        mContext = context.getApplicationContext();
        mListener = listener;
        mTargetPackage = targetPackage;
        mCurrentPackage = "";
    }

    @Override
    public void run() {
        final String packageName = getTopActivityPackage();
        if (!mCurrentPackage.equals(packageName)) {
            // Changed
            mCurrentPackage = packageName == null ? "" : packageName;
            if (mListener != null) {
                if (mCurrentPackage.equals(mTargetPackage)) {
                    mListener.onPackageForeground();
                } else {
                    mListener.onPackageBackground();
                }
            }
        }
    }

    private String getTopActivityPackage() {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(1);
        return infos.get(0).topActivity.getPackageName();
    }

    public interface OnTargetPackageListener {
        void onPackageForeground();
        void onPackageBackground();
    }
}
