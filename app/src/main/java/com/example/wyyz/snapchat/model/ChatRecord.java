package com.example.wyyz.snapchat.model;

import java.util.Date;

/**
 * A ChatRecord from User(senderId) to User(receiverId).
 * Created by ZIYUAN on 3/09/2016.
 */
public class ChatRecord {
    private int id;
    private int senderId;
    private int receiverId;
    private int snapId;
    private int storyId;
    private String content;
    private Date timestamp;
    private int type;
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getSnapId() {
        return snapId;
    }

    public void setSnapId(int snapId) {
        this.snapId = snapId;
    }

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
