package com.perqin.gandear;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.perqin.gandear.common.SummaryPreference;
import com.perqin.gandear.floatingwindow.FloatingWindowServiceHelper;
import com.perqin.gandear.floatingwindow.ScreenshotQuickAddEnabledHelper;
import com.perqin.gandear.topactivity.TopActivityServiceHelper;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment fragment = getFragmentManager().findFragmentById(android.R.id.content);
        if (fragment != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static class SettingsFragment extends PreferenceFragment {
        private static final int REQUEST_MANAGE_OVERLAY_PERMISSION = 6565;
        private static final int REQUEST_READ_EXTERNAL_STORAGE = 10;

        private boolean mDrawOverAppsRequestFlag;
        private boolean mAccessibilityRequestFlag;
        private boolean mToggleOnRequestFlag;

        private SwitchPreference mServiceEnabledPreference;
        private SwitchPreference mScreenshotQuickAddEnabledPreference;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_settings);

            resetAllRequestFlags();

            PreferenceScreen preferenceScreen = getPreferenceScreen();
            mServiceEnabledPreference = (SwitchPreference)
                    preferenceScreen.findPreference(getString(R.string.pk_service_enabled));
            mServiceEnabledPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                Boolean checked = (Boolean) newValue;
                // Avoid restart or re-stop service
                if (checked == TopActivityServiceHelper.isServiceRunning(getActivity())) {
                    return true;
                }
                if (checked) {
                    // Make requests: Draw Over Apps permission, Accessibility Service permission and TopActivityService starting
                    mDrawOverAppsRequestFlag = true;
                    mAccessibilityRequestFlag = true;
                    mToggleOnRequestFlag = true;
                    handleRequests();
                } else {
                    TopActivityServiceHelper.stopService(getActivity());
                }
                return true;
            });
            mScreenshotQuickAddEnabledPreference = (SwitchPreference)
                    preferenceScreen.findPreference(getString(R.string.pk_screenshot_quick_add_enabled));
            mScreenshotQuickAddEnabledPreference.setOnPreferenceChangeListener(((preference, newValue) -> {
                boolean checked = (boolean) newValue;
                if (checked) {
                    if (ScreenshotQuickAddEnabledHelper.canEnable(getActivity())) {
                        return true;
                    } else {
                        // Request permission
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_READ_EXTERNAL_STORAGE);
                        return false;
                    }
                } else {
                    return true;
                }
            }));
            SummaryPreference faqPreference = (SummaryPreference) preferenceScreen.findPreference(getString(R.string.pk_faq));
            faqPreference.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), FaqActivity.class));
                return true;
            });
        }

        @Override
        public void onResume() {
            super.onResume();
            handleRequests();
            refreshSwitchState();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (REQUEST_MANAGE_OVERLAY_PERMISSION == requestCode) {
                if (!FloatingWindowServiceHelper.canStartService(getActivity())) {
                    // The user refuses to grant permission! Reset all flags and don't even try starting service!
                    resetAllRequestFlags();
                }
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mScreenshotQuickAddEnabledPreference.setChecked(true);
                } else {
                    Toast.makeText(getActivity(), R.string.read_external_storage_permission_is_required_to_read_your_screenshot, Toast.LENGTH_SHORT).show();
                }
            }
        }

        /**
         * Handle requests for permission requests and service starting. Call this method whenever
         * you want to start service within the activity context.
         */
        private void handleRequests() {
            // Preset flags
            mDrawOverAppsRequestFlag = !FloatingWindowServiceHelper.canStartService(getActivity()) && mDrawOverAppsRequestFlag;
            mAccessibilityRequestFlag = !TopActivityServiceHelper.canStartService(getActivity()) && mAccessibilityRequestFlag;
            // Handle requested flag in priority: DrawOverApps, AccessibilityService, ToggleOn
            if (mDrawOverAppsRequestFlag) {
                mDrawOverAppsRequestFlag = false;
                if (!FloatingWindowServiceHelper.canStartService(getActivity())) {
                    // Request permission
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.permission_required)
                            .setMessage(R.string.to_show_floating_window_when_you_are_playing_the_game_on_android_lower_than_kitkat_we_need_draw_over_apps_permission_granted)
                            .setPositiveButton(
                                    R.string.enable,
                                    (dialog, which) -> startActivityForResult(new Intent(
                                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:" + getActivity().getPackageName())
                                    ), REQUEST_MANAGE_OVERLAY_PERMISSION)
                            )
                            .setNegativeButton(R.string.cancel, ((dialog, which) -> {
                                resetAllRequestFlags();
                                refreshSwitchState();
                            }))
                            .show();
                }
            } else if (mAccessibilityRequestFlag) {
                mAccessibilityRequestFlag = false;
                if (!TopActivityServiceHelper.canStartService(getActivity())) {
                    // Request accessibility permission
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.permission_required)
                            .setMessage(R.string.to_detect_whether_you_are_playing_the_game_on_android_lollipop_and_higher_we_need_accessibility_service_enabled)
                            .setPositiveButton(R.string.enable, (dialog, which) -> startActivity(new Intent(getString(R.string.action_android_settings_accessibility_settings))))
                            .setNegativeButton(R.string.cancel, ((dialog, which) -> {
                                resetAllRequestFlags();
                                refreshSwitchState();
                            }))
                            .show();
                }
            } else if (mToggleOnRequestFlag) {
                mToggleOnRequestFlag = false;
                // Though the user may not grant permissions, we have already tried :)
                if (FloatingWindowServiceHelper.canStartService(getActivity()) && TopActivityServiceHelper.canStartService(getActivity())) {
                    TopActivityServiceHelper.startService(getActivity());
                } else {
                    Toast.makeText(getActivity(), R.string.we_need_necessary_permissions_to_work_properly, Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void resetAllRequestFlags() {
            mDrawOverAppsRequestFlag = false;
            mAccessibilityRequestFlag = false;
            mToggleOnRequestFlag = false;
        }

        private void refreshSwitchState() {
            mServiceEnabledPreference.setChecked(TopActivityServiceHelper.isServiceRunning(getActivity()));
        }
    }
}
