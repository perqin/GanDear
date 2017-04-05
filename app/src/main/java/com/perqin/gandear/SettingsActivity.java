package com.perqin.gandear;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        private SwitchPreference mServiceEnabledPreference;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_settings);

            PreferenceScreen preferenceScreen = getPreferenceScreen();
            mServiceEnabledPreference = (SwitchPreference)
                    preferenceScreen.findPreference(getString(R.string.pk_service_enabled));
            mServiceEnabledPreference.setChecked(
                    ServiceUtils.isServiceRunning(getActivity(), FloatingWindowService.class));
            mServiceEnabledPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                Boolean checked = (Boolean) newValue;
                Intent intent = new Intent(getActivity(), FloatingWindowService.class);
                if (checked) {
                    getActivity().startService(intent);
                } else {
                    getActivity().stopService(intent);
                }
                return true;
            });
        }
    }
}
