package com.example.wyyz.snapchat.model;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A snap is a picture.
 * Created by ZIYUAN on 3/09/2016.
 */
public class MyStorySnap {
    private String path;
    private int timingOut = 3;
    private Date timestamp;
    //private String location;

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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        try {
            this.timestamp = format.parse(timestamp);
        }catch(Exception e)
        {}
    }

}
