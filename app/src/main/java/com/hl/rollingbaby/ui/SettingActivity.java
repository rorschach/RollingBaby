package com.hl.rollingbaby.ui;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;

public class SettingActivity extends ActionBarActivity {

    public static final String TAG = "SettingActivity";
    public static final String IP_ADDRESS_PORT = "IP_ADDRESS_PORT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_setting);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle(R.string.app_name);
//        setSupportActionBar(toolbar);
        getFragmentManager().beginTransaction().replace(
                android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
//
        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
            Log.d(TAG, "onResume");
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
            Log.d(TAG, "onPause");
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            if (key.equals(IP_ADDRESS_PORT)) {
                Preference pref = this.findPreference(key);
                // Set summary to be the user-description for the selected value
                pref.setSummary(getActivity().getResources().getString(R.string.summary_header)
                        + sharedPreferences.getString(key, Constants.ADDRESS_PORT));
                Log.d("TAG", pref.getSummary().toString());
            }
            Log.d(TAG, "onSharedPreferenceChanged");
        }
    }

}
