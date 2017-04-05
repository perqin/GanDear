package com.perqin.gandear;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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
                if (checked == ServiceUtils.isServiceRunning(getActivity(), FloatingWindowService.class)) {
                    return true;
                }
                if (checked) {
                    // We have to handle permission here
                    tryStartingFloatingWindowService();
                } else {
                    Intent intent = new Intent(getActivity(), FloatingWindowService.class);
                    getActivity().stopService(intent);
                }
                return true;
            });
        }

        @Override
        public void onResume() {
            super.onResume();
            mServiceEnabledPreference.setChecked(
                    ServiceUtils.isServiceRunning(getActivity(), FloatingWindowService.class));
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (REQUEST_MANAGE_OVERLAY_PERMISSION == requestCode) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(getActivity())) {
                        // User refuse to grant permission :(
                        Toast.makeText(getActivity(), R.string.you_must_allow_me_to_draw_over_other_apps, Toast.LENGTH_SHORT).show();
                    } else {
                        startFloatingWindowService();
                    }
                }
            }
        }

        private void tryStartingFloatingWindowService() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // "Draw over other apps" has to be enable manually by user on Android M
                // if the app is not installed from Google Play Store.
                if (!Settings.canDrawOverlays(getActivity())) {
                    Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + "com.perqin.gandear"));
                    startActivityForResult(permissionIntent, REQUEST_MANAGE_OVERLAY_PERMISSION);
                } else {
                    startFloatingWindowService();
                }
            } else {
                startFloatingWindowService();
            }
        }

        private void startFloatingWindowService() {
            Intent intent = new Intent(getActivity(), FloatingWindowService.class);
            getActivity().startService(intent);
        }
    }
}
