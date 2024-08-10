package com.example.ojtbadaassignment14.models;

import java.io.Serializable;

public class DateData implements Serializable {
    private Long time;
    public boolean isSelected;

    public DateData(Long time, boolean isSelected) {
        this.time = time;
        this.isSelected = isSelected;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
