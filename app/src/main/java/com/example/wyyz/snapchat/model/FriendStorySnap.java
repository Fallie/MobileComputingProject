package com.example.wyyz.snapchat.model;

import android.graphics.Bitmap;
import android.util.Base64;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by leify on 2016/10/17.
 */
public class FriendStorySnap {
    private FirebaseAuth mAuth;
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



        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        DatabaseReference channelRef = ref.child("MyStoryVisitRecord");

        String caseName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(getTimestamp());

        String userId = mAuth.getInstance().getCurrentUser().getUid();
            channelRef = ref.child("MyStoryVisitRecord").child(getUserId()).child(caseName).child(userId);

            Map<String, Object> updates = new HashMap<String, Object>();
            updates.put("visitNum", getVisitNum());

            channelRef.updateChildren(updates);



    }

}