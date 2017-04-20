package com.perqin.gandear;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimerTask;

/**
 * Author   : perqin
 * Date     : 17-4-17
 */

public class CurrentActivityTimerTask extends TimerTask {
    private static Comparator<UsageStats> sRecentComp = new Comparator<UsageStats>() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public int compare(UsageStats lhs, UsageStats rhs) {
            return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) ? -1 : (lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) ? 0 : 1;
        }
    };

    private Context mContext;
    private OnTargetPackageListener mListener;
    private String mTargetPackage;
    private String mCurrentPackage;

    public CurrentActivityTimerTask(Context context, OnTargetPackageListener listener, String targetPackage) {
        mContext = context.getApplicationContext();
        mListener = listener;
        mTargetPackage = targetPackage;
        mCurrentPackage = "";
    }

    @Override
    public void run() {
        final String packageName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            packageName = getTopActivityPackageNewApi();
        } else {
            packageName = getTopActivityPackageLegal();
        }
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

    private String getTopActivityPackageLegal() {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(1);
        return infos.get(0).topActivity.getPackageName();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private String getTopActivityPackageNewApi() {
        long t = System.currentTimeMillis();
        UsageStatsManager usm = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, t - 1000, t);
        if (stats == null || stats.isEmpty()) {
            return "";
        }
        Collections.sort(stats, sRecentComp);
        return stats.get(0).getPackageName();
    }

    public interface OnTargetPackageListener {
        void onPackageForeground();
        void onPackageBackground();
    }
}
