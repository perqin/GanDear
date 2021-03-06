package com.perqin.gandear.floatingwindow;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;

import com.perqin.gandear.common.ServiceUtils;
import com.perqin.gandear.floatingwindow.services.FloatingWindowService;

/**
 * Author   : perqin
 * Date     : 17-4-20
 */

public class FloatingWindowServiceHelper {
    private static boolean sStopDisabledFlag = false;

    public static boolean canStartService(Context context) {
        // "Draw over other apps" has to be enable manually by user on Android M
        // if the app is not installed from Google Play Store.
        // We cannot use TYPE_TOAST on Android 7.1 and higher
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1 || Settings.canDrawOverlays(context);
    }

    public static boolean isServiceRunning(Context context) {
        return ServiceUtils.isServiceRunning(context, FloatingWindowService.class);
    }

    public static void startService(Context context) {
        context.startService(new Intent(context, FloatingWindowService.class));
    }

    public static void stopService(Context context) {
        if (sStopDisabledFlag) return;
        context.stopService(new Intent(context, FloatingWindowService.class));
    }

    public static void setStopDisabledFlag(boolean stopDisabledFlag) {
        sStopDisabledFlag = stopDisabledFlag;
    }

    public static int getFloatingWindowType() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            // On Android lower than API 19, TYPE_TOAST cannot receive touch event
            // While on Android API 25 (7.1) or higher, TYPE_TOAST is forced to fade out after some time
            return WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            return WindowManager.LayoutParams.TYPE_TOAST;
        }
    }
}
