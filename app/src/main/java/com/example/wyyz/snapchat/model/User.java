package com.example.wyyz.snapchat.model;

/**
 * Created by ZIYUAN on 3/09/2016.
 */
public class User {
    private int id;
    private String email;
    private String username;
    private String birthday;
    private String mobile;
    private String avatar;
    private String QRcode;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /*public Bitmap getQRcode() {
        return QRcode;
    }

    public void setQRcode(Bitmap myBitmap) {
        this.QRcode=myBitmap;
    }*/
    public String getQRcode(){
        return QRcode;
    }
    public void setQRcode(String myBitmap){
        if(myBitmap==null){
            this.QRcode=null;
        }else {
            this.QRcode = myBitmap.toString();
        }
    }
}
