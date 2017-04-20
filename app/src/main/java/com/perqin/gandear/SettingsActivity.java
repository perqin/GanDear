package com.perqin.gandear;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.perqin.gandear.topactivity.TopActivityServiceHelper;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        private static final int REQUEST_MANAGE_OVERLAY_PERMISSION = 6565;

        private SwitchPreference mServiceEnabledPreference;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_settings);

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
                    // Start top activity service
                    if (TopActivityServiceHelper.canStartService(getActivity())) {
                        TopActivityServiceHelper.startService(getActivity());
                    } else {
                        getPermissionAndStartTopActivityService();
                    }
                } else {
                    TopActivityServiceHelper.stopService(getActivity());
                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                     Use accessibility service
//                    if (checked) {
//                         TODO: Request permission -> enable service -> show service immediately is top activity shown
//                    } else {
//                    }
//                } else {
//                     Use watching service
//                }
//                if (checked) {
//                     We have to handle permission here
//                    tryStartingFloatingWindowService();
//                } else {
//                    Intent intent = new Intent(getActivity(), FloatingWindowService.class);
//                    getActivity().stopService(intent);
//                }
                return true;
            });
        }

        @Override
        public void onResume() {
            super.onResume();
            refreshSwitchState();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
//            if (REQUEST_MANAGE_OVERLAY_PERMISSION == requestCode) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (!Settings.canDrawOverlays(getActivity())) {
//                        // User refuse to grant permission :(
//                        Toast.makeText(getActivity(), R.string.you_must_allow_me_to_draw_over_other_apps, Toast.LENGTH_SHORT).show();
//                    } else {
//                        startFloatingWindowService();
//                    }
//                }
//            }
        }

        private void getPermissionAndStartTopActivityService() {
            // We don't have to handle result in onActivityResult, because once the user grant us
            // permission, the accessibility service is running. We just have to consider the
            // situation when user refuses to grant permission.
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.permission_required)
                    .setMessage(R.string.to_detect_whether_you_are_playing_the_game_on_android_lollipop_and_higher_we_need_accessibility_service_enabled)
                    .setPositiveButton(R.string.enable, (dialog, which) -> {
                        TopActivityServiceHelper.forceStartService(getActivity());
                        Intent intent = new Intent(getString(R.string.action_android_settings_accessibility_settings));
                        startActivity(intent);
                    })
                    .setNegativeButton(R.string.cancel, ((dialog, which) -> {
                        refreshSwitchState();
                    }))
                    .show();
        }

        private void refreshSwitchState() {
            mServiceEnabledPreference.setChecked(TopActivityServiceHelper.isServiceRunning(getActivity()));
        }

//        private void tryStartingFloatingWindowService() {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                // "Draw over other apps" has to be enable manually by user on Android M
//                // if the app is not installed from Google Play Store.
//                if (!Settings.canDrawOverlays(getActivity())) {
//                    Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + "com.perqin.gandear"));
//                    startActivityForResult(permissionIntent, REQUEST_MANAGE_OVERLAY_PERMISSION);
//                } else {
//                    startFloatingWindowService();
//                }
//            } else {
//                startFloatingWindowService();
//            }
//        }
//
//        private void startFloatingWindowService() {
//            Intent intent = new Intent(getActivity(), FloatingWindowService.class);
//            getActivity().startService(intent);
//        }
    }
}
