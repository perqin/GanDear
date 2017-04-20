package com.perqin.gandear.topactivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.perqin.gandear.common.ServiceUtils;

/**
 * Author   : perqin
 * Date     : 17-4-19
 */

public class TopActivityServiceHelper {
    private static final String PK_IS_ACCESSIBILITY_SERVICE_RUNNING = "IS_ACCESSIBILITY_SERVICE_RUNNING";

    public static boolean canStartService(Context context) {
        // The service is running when and only when the accessibility permission is granted
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
                || ServiceUtils.isServiceRunning(context, TopActivityAccessibilityService.class);
    }

    public static boolean isServiceRunning(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!canStartService(context)) {
                // Resolve inconsistency
                getSp(context).edit().putBoolean(PK_IS_ACCESSIBILITY_SERVICE_RUNNING, false).apply();
            }
            return getSp(context).getBoolean(PK_IS_ACCESSIBILITY_SERVICE_RUNNING, false);
        } else {
            return ServiceUtils.isServiceRunning(context, TopActivityWatchingService.class);
        }
    }

    public static void forceStartService(Context context) {
        getSp(context).edit().putBoolean(PK_IS_ACCESSIBILITY_SERVICE_RUNNING, true).apply();
    }

    public static void startService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSp(context).edit().putBoolean(PK_IS_ACCESSIBILITY_SERVICE_RUNNING, true).apply();
        } else {
            context.startService(new Intent(context, TopActivityWatchingService.class));
        }
    }

    public static void stopService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSp(context).edit().putBoolean(PK_IS_ACCESSIBILITY_SERVICE_RUNNING, false).apply();
        } else {
            context.stopService(new Intent(context, TopActivityWatchingService.class));
        }
    }

    private static SharedPreferences getSp(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }
}
