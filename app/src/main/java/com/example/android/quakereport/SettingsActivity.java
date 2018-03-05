package com.example.android.quakereport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by harsh24 on 5/3/18.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this,EarthquakeActivity.class);
        startActivity(intent);
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMagnitude);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference limit = findPreference(getString(R.string.settings_limit_key));
            bindPreferenceSummaryToValue(limit);

            Preference startTime = findPreference(getString(R.string.settings_start_time_key));
            bindPreferenceSummaryToValue(startTime);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            }
            else if (preference instanceof DatePreference) {
                String summary;
                Date date = null;
                DatePreference datePreference = (DatePreference) preference;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy");

                if(String.valueOf(newValue).isEmpty()){
                    try {
                        date = new SimpleDateFormat("yyyy-M-d").parse(getString(R.string.settings_start_time_default));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    try {
                        date = new SimpleDateFormat("yyyy-M-d").parse(stringValue);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                summary = simpleDateFormat.format(date);
                datePreference.setSummary(summary);
            }
            else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
