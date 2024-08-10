package com.example.ojtbadaassignment14.dialog;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.preference.DialogPreference;

import com.example.ojtbadaassignment14.R;

public class SeekBarDialogPreference extends DialogPreference {

    private int mValue;

    public SeekBarDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.preference_dialog_seekbar);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        if (defaultValue == null) {
            defaultValue = 0;
        }

        setValue(getPersistedInt((Integer) defaultValue));
    }

    public void setValue(int value) {
        mValue = value;
        persistInt(value);
        notifyChanged();
    }

    public int getValue() {
        return mValue;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }
}
