package com.github.masdaster.edma.utils;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import androidx.annotation.StringRes;

public class SettingsUtils {
    public static boolean getBoolean(Context c, String key, Boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(c)
                .getBoolean(key, defaultValue);
    }

    public static int getInt(Context c, String key) {
        return PreferenceManager.getDefaultSharedPreferences(c)
                .getInt(key, -1);
    }

    public static void setInt(Context c, String key, int value) {
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferenceManager.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String getString(Context c, String key) {
        return getString(c, key, "");
    }

    public static String getString(Context c, String key, @StringRes int defValue) {
        return getString(c, key, c.getString(defValue));
    }

    public static String getString(Context c, String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(c)
                .getString(key, defValue);
    }


    public static void setString(Context c, String key, String value) {
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferenceManager.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
