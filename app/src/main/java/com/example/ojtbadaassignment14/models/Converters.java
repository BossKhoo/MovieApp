package com.example.ojtbadaassignment14.models;

import androidx.room.TypeConverter;
import java.util.ArrayList;
import java.util.Arrays;

public class Converters {

    @TypeConverter
    public static ArrayList<Integer> fromString(String value) {
        ArrayList<Integer> list = new ArrayList<>();
        if (value != null && !value.isEmpty()) {
            String[] values = value.split(",");
            for (String v : values) {
                list.add(Integer.parseInt(v));
            }
        }
        return list;
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Integer> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Integer i : list) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(i);
        }
        return sb.toString();
    }
}
