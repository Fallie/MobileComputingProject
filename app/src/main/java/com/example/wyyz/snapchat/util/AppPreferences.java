package com.example.wyyz.snapchat.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.wyyz.snapchat.activity.MyApplication;


public class AppPreferences {

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private int MODE = -1;
    private static final int READ = 0, WRITE = 1;

    public AppPreferences(String prefName, int MODE) {
        sharedPrefs = MyApplication.getApplicationInstance().getSharedPreferences(prefName, Context.MODE_PRIVATE);

        switch (MODE) {
            case READ:
                break;
            case WRITE:
                    editor = sharedPrefs.edit();
                break;
        }
    }

    public AppPreferences putInt(String key, int value) {
        if (editor != null) {
            editor.putInt(key, value);
            return this;
        } else {
            throw new RuntimeException("Editor cannot be null, please check if you have chosen WRITE MODE");
        }
    }

    public int getInt(String key, int defValue) {
        return sharedPrefs.getInt(key, defValue);
    }

    public AppPreferences putString(String key, String value) {
        if (editor != null) {
            editor.putString(key, value);
            return this;
        } else {
            throw new RuntimeException("Editor cannot be null, please check if you have chosen WRITE MODE");
        }
    }

    public String getString(String key, String defValue) {
        return sharedPrefs.getString(key, defValue);
    }

    public AppPreferences putBoolean(String key, boolean value) {
        if (editor != null) {
            editor.putBoolean(key, value);
            return this;
        } else {
            throw new RuntimeException("Editor cannot be null, please check if you have chosen WRITE MODE");
        }

    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPrefs.getBoolean(key, defValue);
    }

    public AppPreferences putLong(String key, long value) {
        if (editor != null) {
            editor.putLong(key, value);
            return this;
        } else {
            throw new RuntimeException("Editor cannot be null, please check if you have chosen WRITE MODE");
        }
    }

    public long getLong(String key, long defValue) {
        return sharedPrefs.getLong(key, defValue);
    }

    public AppPreferences commit() {
        if (editor != null) {
            editor.commit();
            return this;
        } else {
            throw new RuntimeException("Editor cannot be null, please check if you have chosen WRITE MODE");
        }
    }

    public AppPreferences remove(String key) {
        if (editor != null) {
            editor.remove(key);
            return this;
        } else {
            throw new RuntimeException("Editor cannot be null, please check if you have chosen WRITE MODE");
        }
    }

    public AppPreferences clearAllPreferences() {
        if (editor != null) {
            editor.clear().commit();
            return this;
        } else {
            throw new RuntimeException("Editor cannot be null, please check if you have chosen WRITE MODE");
        }
    }
}