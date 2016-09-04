package com.example.wyyz.snapchat.model;

import java.util.Date;
import java.util.List;

/**
 * A story is made up by several snaps.
 * Created by ZIYUAN on 3/09/2016.
 */
public class Story {
    private int id;
    private String name;
    private Date timestamp;
    private boolean locked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

}
