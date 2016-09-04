package com.example.wyyz.snapchat.model;


import java.util.Date;

/**
 * An instance of this class is one friend of User(ownerId),
 * its fields involve all the information that can be seen by user(ownerId).
 * Created by ZIYUAN on 3/09/2016.
 */
public class Friends {
    private int ownerId;
    private int friendId;
    private Date createTime;
    private String editedName;
    private boolean blocked;
    private Date lastChatTimeStamp;
    private String userName;
    private String avatar;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getQRcode() {
        return QRcode;
    }

    public void setQRcode(String QRcode) {
        this.QRcode = QRcode;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private String QRcode;
    private String nickName;

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getEditedName() {
        return editedName;
    }

    public void setEditedName(String editedName) {
        this.editedName = editedName;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public Date getLastChatTimeStamp() {
        return lastChatTimeStamp;
    }

    public void setLastChatTimeStamp(Date lastChatTimeStamp) {
        this.lastChatTimeStamp = lastChatTimeStamp;
    }
}
