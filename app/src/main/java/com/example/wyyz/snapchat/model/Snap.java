package com.example.wyyz.snapchat.model;


import android.graphics.Bitmap;

/**
 * A snap is a picture.
 * Created by ZIYUAN on 3/09/2016.
 */
public class Snap {
    private int id;
    private int userId;
    private boolean inMemory = false;
    private Bitmap photo;
    private String path;
    private int timingOut = 3;
    private String timestamp;
    private boolean isChecked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isInMemory() {
        return inMemory;
    }

    public void setInMemory(boolean inMemory) {
        this.inMemory = inMemory;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTimingOut() {
        return timingOut;
    }

    public void setTimingOut(int timingOut) {
        this.timingOut = timingOut;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
    public Bitmap getPhoto() {
        return photo;
    }

    public boolean getChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}