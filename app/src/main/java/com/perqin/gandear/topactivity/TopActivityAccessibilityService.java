package com.perqin.gandear.topactivity;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

/**
 * Author   : perqin
 * Date     : 17-4-18
 */

public class TopActivityAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = event.getPackageName().toString();
            // TODO: Show or hide window
            Toast.makeText(this, "Package name: " + packageName, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInterrupt() {
    }
}
