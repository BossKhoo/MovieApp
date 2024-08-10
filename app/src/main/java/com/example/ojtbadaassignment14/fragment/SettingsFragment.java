package com.example.ojtbadaassignment14.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.ojtbadaassignment14.R;
import com.example.ojtbadaassignment14.dialog.SeekBarDialogPreference;
import com.example.ojtbadaassignment14.dialog.SeekBarDialogPreferenceFragmentCompat;
import com.example.ojtbadaassignment14.livedata.FavouriteMovieViewModel;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    public SettingsFragment() {
    }

//    FavouriteMovieViewModel movieViewModel = new FavouriteMovieViewModel();

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference ratePreference = findPreference("rate_preference");
        if (ratePreference != null) {
            ratePreference.setSummaryProvider(preference -> {
                SeekBarDialogPreference seekBarDialogPreference = (SeekBarDialogPreference) preference;
                return String.valueOf(seekBarDialogPreference.getValue());
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof SeekBarDialogPreference) {
            SeekBarDialogPreferenceFragmentCompat dialogFragment =
                    SeekBarDialogPreferenceFragmentCompat.newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getParentFragmentManager(), null);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {

    }


}
