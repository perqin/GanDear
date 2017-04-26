package com.perqin.gandear.ocr;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Author   : perqin
 * Date     : 17-4-21
 */

public class ScreenshotHelper {
    private static final String PK_SCREENSHOT_DETECTION_ENABLED = "SCREENSHOT_DETECTION_ENABLED";

    public static void enableDetection(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        sp.edit().putBoolean(PK_SCREENSHOT_DETECTION_ENABLED, true).apply();
    }

    public static void disableDetection(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        sp.edit().putBoolean(PK_SCREENSHOT_DETECTION_ENABLED, false).apply();
    }

    public static boolean isDetectionEnabled(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(PK_SCREENSHOT_DETECTION_ENABLED, false);
    }
}
