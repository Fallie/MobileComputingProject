package com.example.wyyz.snapchat.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

/**
 * Created by leify on 2016/10/14.
 */

public class DiscoveryChannel {

    private int channelId;
    private int visitNum;
    private String name;
    private boolean subscriptionState;
    private Bitmap profile;
    private ArrayList<String> contents;

    public DiscoveryChannel(int channelId, String name, int visitNum, int subscriptionState, byte[] profile, ArrayList<String> contents)
    {
        this.channelId = channelId;

        this.name =name;

        this.visitNum = visitNum;

        if(subscriptionState ==0)
            this.subscriptionState = false;
        else if(subscriptionState == 1)
            this.subscriptionState = true;

        this.profile = BitmapFactory.decodeByteArray(profile, 0, profile.length);

        this.contents = contents;
    }

    public ArrayList<String> getContents() {
        return contents;
    }

    public Bitmap getProfile() {
        return profile;
    }

    public boolean isSubscriptionState() {
        return subscriptionState;
    }

    public int getChannelId() {
        return channelId;
    }

    public String getName() {
        return name;
    }

    public int getVisitNum() {
        return visitNum;
    }

    public void subscribe() {
        this.subscriptionState = true;
    }

    public void unsubscribe() {
        this.subscriptionState = false;
    }

    public void visit() {
        this.visitNum++;
    }
}
