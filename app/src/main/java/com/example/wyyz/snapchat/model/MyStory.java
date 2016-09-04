package com.example.wyyz.snapchat.model;


import java.util.Date;

/**
 * Created by ZIYUAN on 3/09/2016.
 */
public class MyStory {
    private int id;
    private int userId;
    private int storyId;
    private int snapId;
    private Date timestamp;
    private boolean whetherPublic;
    private int snapNum;

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

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public int getSnapId() {
        return snapId;
    }

    public void setSnapId(int snapId) {
        this.snapId = snapId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isWhetherPublic() {
        return whetherPublic;
    }

    public void setWhetherPublic(boolean whetherPublic) {
        this.whetherPublic = whetherPublic;
    }

    public int getSnapNum() {
        return snapNum;
    }

    public void setSnapNum(int snapNum) {
        this.snapNum = snapNum;
    }
}
