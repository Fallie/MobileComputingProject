package com.example.wyyz.snapchat.model;

import android.graphics.Bitmap;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by leify on 2016/10/16.
 */

public class FriendStory {

    private ArrayList<String> snaps = new ArrayList<String>();
    private String storyName;
    private Date storyTime;
    private int visitNum;
    private Bitmap preview;

    public FriendStory(String name)
    {
        storyName = name;
//        storyTime=new Time(Time.getCurrentTimezone()); // or Time t=new Time("GMT+8"); 加上Time Zone资料
//        storyTime.setToNow();
        storyTime = Calendar.getInstance().getTime();
        visitNum =0;

    }

    public ArrayList<String> getSnaps()
    {
        return this.snaps;
    }

    public void addSnap(String snap)
    {
        this.snaps.add(snap);
    }


    public String getName()
    {
        return storyName;
    }

    public int getVisitNum()
    {
        return visitNum;
    }

    public void visit()
    {
        this.visitNum++;
    }

    public String getTimeStamp()
    {
        try {
            SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(storyTime);
            return date;
        }catch(Exception e)
        {
            Log.e("error",e.getMessage());
            e.printStackTrace();
            return  null;
        }
    }
}
