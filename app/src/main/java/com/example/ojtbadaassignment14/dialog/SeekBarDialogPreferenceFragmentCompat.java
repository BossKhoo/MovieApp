package com.example.ojtbadaassignment14.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.example.ojtbadaassignment14.R;

public class SeekBarDialogPreferenceFragmentCompat extends PreferenceDialogFragmentCompat {

    private SeekBar mSeekBar;
    private int mValue;

    public static SeekBarDialogPreferenceFragmentCompat newInstance(String key) {
        final SeekBarDialogPreferenceFragmentCompat fragment = new SeekBarDialogPreferenceFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mSeekBar = view.findViewById(R.id.seekbar);

        if (mSeekBar == null) {
            throw new IllegalStateException("Dialog view must contain a SeekBar with id 'seekbar'");
        }

        SeekBarDialogPreference preference = (SeekBarDialogPreference) getPreference();
        mValue = preference.getValue();
        mSeekBar.setProgress(mValue);
        mSeekBar.setMax(5);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SeekBarDialogPreference preference = (SeekBarDialogPreference) getPreference();
            if (preference.callChangeListener(mValue)) {
                preference.setValue(mValue);
            }
        }
    }
}
