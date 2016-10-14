package com.example.wyyz.snapchat.activity;

import android.app.Application;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.example.wyyz.snapchat.util.FirebaseUtility;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();
    private static MyApplication instance;
    private boolean isChatActivityInForeground = false;
    private static Gson gson;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        FirebaseUtility.initialize();
    }

    /**
     * get application instance
     *
     * @return
     */
    public static MyApplication getApplicationInstance() {
        return instance;
    }

    public static EventBus getEventBusInstance(){
        return EventBus.getDefault();
    }

    public boolean isChatActivityInForeground() {
        return isChatActivityInForeground;
    }

    public void setChatActivityInForeground(boolean chatActivityInForeground) {
        isChatActivityInForeground = chatActivityInForeground;
    }


    public static Gson getGsonInstance(){
        if(gson == null){
            gson = new Gson();
        }
        return gson;
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    /**
     * show Toast message
     * @param message
     */
    public static void showToast(String message) {
        Toast.makeText(getApplicationInstance(), message, Toast.LENGTH_LONG).show();
    }

    public static void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}
