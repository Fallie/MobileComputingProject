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

    private ArrayList<MyStorySnap> snaps = new ArrayList<MyStorySnap>();
    private String storyName;
    private Date storyTime;
    private int visitNum;

    public FriendStory(String name)
    {
        storyName = name;
//        storyTime=new Time(Time.getCurrentTimezone()); // or Time t=new Time("GMT+8"); 加上Time Zone资料
//        storyTime.setToNow();
        storyTime = Calendar.getInstance().getTime();
        visitNum =0;

    }

    public ArrayList<MyStorySnap> getSnaps()
    {
        return this.snaps;
    }

    public void addSnap(MyStorySnap snap)
    {
        this.snaps.add(snap);

        parseSnaps();

        if(snaps.size()>0)
            storyTime = snaps.get(snaps.size()-1).getTimestamp();
    }

    public void addSnaps(ArrayList<MyStorySnap> snaps)
    {
        for(MyStorySnap snap : snaps)
            this.snaps.add(snap);

        parseSnaps();

        if(snaps.size()>0)
            storyTime = snaps.get(snaps.size()-1).getTimestamp();
    }

    private void parseSnaps()
    {
        ArrayList<MyStorySnap> tmpSnaps = new ArrayList<MyStorySnap>();
        Date date = Calendar.getInstance().getTime();
        for(MyStorySnap snap : snaps)
        {
            Date snapDate = snap.getTimestamp();

            long day = date.getTime() / (24*60*60*1000) - snapDate.getTime() / (24*60*60*1000);

            if(!(day>1))
                tmpSnaps.add(snap);
        }
        this.snaps = tmpSnaps;
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
