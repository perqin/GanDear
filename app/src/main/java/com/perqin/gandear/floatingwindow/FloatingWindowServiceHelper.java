package com.perqin.gandear.floatingwindow;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import com.perqin.gandear.common.ServiceUtils;
import com.perqin.gandear.floatingwindow.services.FloatingWindowService;

/**
 * Author   : perqin
 * Date     : 17-4-20
 */

public class FloatingWindowServiceHelper {
    public static boolean canStartService(Context context) {
        // "Draw over other apps" has to be enable manually by user on Android M
        // if the app is not installed from Google Play Store.
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }

    public static boolean isServiceRunning(Context context) {
        return ServiceUtils.isServiceRunning(context, FloatingWindowService.class);
    }

    public static void startService(Context context) {
        context.startService(new Intent(context, FloatingWindowService.class));
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, FloatingWindowService.class));
    }
}
