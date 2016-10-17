package com.example.wyyz.snapchat.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by leify on 2016/10/17.
 */
public class FriendStorySnap {
    private String userName;
    private String  userId;
    private String path;
    private int timingOut = 3;
    private Date timestamp;
    private int visitNum;
    //private String location;


    public void setVisitNum(int visitNum) {
        this.visitNum = visitNum;
    }

    public int getVisitNum() {
        return visitNum;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public void visit()
    {
        this.visitNum++;
    }

}