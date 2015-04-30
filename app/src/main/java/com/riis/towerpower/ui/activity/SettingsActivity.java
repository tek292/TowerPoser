package com.riis.towerpower.ui.activity;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;

import com.riis.towerpower.R;
import com.riis.towerpower.models.Consts;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener
{
    private EditTextPreference mEditTextPreference;
    private ListPreference mListPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        mEditTextPreference = (EditTextPreference) findPreference(getString(R.string.pref_distance_key));
        mListPreference = (ListPreference) findPreference(getString(R.string.pref_units_key));

        mEditTextPreference.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()) {
                    Double doubleValue = Double.parseDouble(s.toString());
                    Double kilometerValue = Consts.convertMilesToKilometers(doubleValue);

                    if(kilometerValue > 16.0934)
                    {
                        mEditTextPreference.getEditText().setError(getString(R.string.pref_distance_error));
                    }
                    else
                    {
                        mEditTextPreference.getEditText().setError(null);
                    }
                } else {
                    mEditTextPreference.getEditText().setError(null);
                }
            }
        });

        bindPreferenceSummaryToValue(mEditTextPreference);
        bindPreferenceSummaryToValue(mListPreference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        String stringValue = newValue.toString();

        if(preference instanceof EditTextPreference)
        {
            if(((EditTextPreference) preference).getEditText().getError() != null)
            {
                return false;
            }
            else
            {
                int distanceValue;
                if(stringValue.isEmpty()) {
                    distanceValue = Integer.parseInt(getString(R.string.pref_distance_default));
                } else {
                    distanceValue = Integer.parseInt(stringValue);
                }

                String unit = mListPreference.getEntry().toString();

                if(unit.equals(getString(R.string.pref_units_metric)))
                {
                    preference.setSummary(getString(R.string.pref_distance_km, Integer.toString(distanceValue)));
                }
                else
                {
                    preference.setSummary(getString(R.string.pref_distance_mi, Integer.toString(distanceValue)));
                }
            }
        }
        else if (preference instanceof ListPreference)
        {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);

                String distanceValue = mEditTextPreference.getText();
                if(listPreference.getEntryValues()[prefIndex].equals(getString(R.string.pref_units_metric)))
                {
                    mEditTextPreference.setSummary(getString(R.string.pref_distance_km, distanceValue));
                }
                else
                {
                    mEditTextPreference.setSummary(getString(R.string.pref_distance_mi, distanceValue));
                }
            }
        }
        else
        {
            preference.setSummary(stringValue);
        }
        return true;
    }

    private void bindPreferenceSummaryToValue(Preference preference)
    {
        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}
