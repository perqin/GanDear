package com.perqin.gandear.floatingwindow;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Author   : perqin
 * Date     : 17-4-26
 */

public class ScreenshotQuickAddEnabledHelper {
    public static boolean canEnable(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN
                || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isEnabled(Context context) {
        return false;
    }
}
