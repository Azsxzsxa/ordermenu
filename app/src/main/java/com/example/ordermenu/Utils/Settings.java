package com.example.ordermenu.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Settings {
    private static Settings INSTANCE;

    private Settings() {
    }

    public static Settings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Settings();
        }
        return INSTANCE;
    }

    private void writeString(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private String readString(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static String getShortDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }
}
