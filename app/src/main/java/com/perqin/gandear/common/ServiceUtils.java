package com.perqin.gandear.common;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Author   : perqin
 * Date     : 17-4-5
 */

public class ServiceUtils {
    private ServiceUtils() {
        // Prevent construction
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info : am.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
