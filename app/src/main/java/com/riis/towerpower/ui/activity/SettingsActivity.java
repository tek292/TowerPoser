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
                    if(mListPreference.getEntry().toString().equals(getString(R.string.pref_units_label_kilometer)))
                    {
                        if(doubleValue > 10)
                        {
                            mEditTextPreference.getEditText().setError(getString(R.string.pref_distance_km_error));
                        }
                        else
                        {
                            mEditTextPreference.getEditText().setError(null);
                        }
                    }
                    else
                    {
                        if(doubleValue > 6)
                        {
                            mEditTextPreference.getEditText().setError(getString(R.string.pref_distance_mi_error));
                        }
                        else
                        {
                            mEditTextPreference.getEditText().setError(null);
                        }
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
                double distanceValue;
                if(stringValue.isEmpty()) {
                    distanceValue = Double.parseDouble(getString(R.string.pref_distance_default));
                } else {
                    distanceValue = Double.parseDouble(stringValue);
                }

                String currentUnit = mListPreference.getEntry().toString();

                if(currentUnit.equals(getString(R.string.pref_units_label_mile)))
                {
                    preference.setSummary(getString(R.string.pref_distance_mi, distanceValue));
                }
                else
                {
                    preference.setSummary(getString(R.string.pref_distance_km, distanceValue));
                }
            }
        }
        else if (preference instanceof ListPreference)
        {
            int prefIndex = mListPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0)
            {
                String currentUnit = mListPreference.getEntry().toString();
                String newUnit = mListPreference.getEntries()[prefIndex].toString();
                mListPreference.setSummary(newUnit);

                if(!currentUnit.equals(newUnit))
                {
                    String distanceValue = mEditTextPreference.getText();

                    if(newUnit.equals(getString(R.string.pref_units_label_mile)))
                    {
                        String newDistance = Double.toString(
                                Consts.convertKilometersToMiles(Double.parseDouble(distanceValue)));
                        mEditTextPreference.setText(newDistance);
                        mEditTextPreference.setSummary(getString(R.string.pref_distance_mi, newDistance));
                    }
                    else
                    {
                        String newDistance = Double.toString(
                                Consts.convertMilesToKilometers(Double.parseDouble(distanceValue)));
                        mEditTextPreference.setText(newDistance);
                        mEditTextPreference.setSummary(getString(R.string.pref_distance_km, newDistance));
                    }
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
