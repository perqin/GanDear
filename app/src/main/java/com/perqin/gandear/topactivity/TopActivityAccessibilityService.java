package com.perqin.gandear.topactivity;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.perqin.gandear.R;
import com.perqin.gandear.floatingwindow.FloatingWindowServiceHelper;

/**
 * Author   : perqin
 * Date     : 17-4-18
 */

public class TopActivityAccessibilityService extends AccessibilityService {
    private static final String TAG = "TopActivityAccessibilit";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // This service is always running
        if (TopActivityServiceHelper.isServiceRunning(this) && AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.getEventType()) {
            String packageName = event.getPackageName().toString();
            Log.i(TAG, "onAccessibilityEvent: " + packageName);
            if (packageName.equals(getPackageName())) {
                // Floating window shown will cause this event!!!
                return;
            }
            if (packageName.equals(TopActivityServiceHelper.PACKAGE_NAME_ONMYOJI)) {
                // Should show
                if (FloatingWindowServiceHelper.canStartService(this)) {
                    if (!FloatingWindowServiceHelper.isServiceRunning(this)) {
                        FloatingWindowServiceHelper.startService(this);
                    }
                } else {
                    Toast.makeText(this, R.string.to_show_floating_window_when_you_are_playing_the_game_on_android_lower_than_kitkat_we_need_draw_over_apps_permission_granted, Toast.LENGTH_SHORT).show();
                }
            } else {
                // Should hide
                if (FloatingWindowServiceHelper.isServiceRunning(this)) {
                    FloatingWindowServiceHelper.stopService(this);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
    }
}
