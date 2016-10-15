package com.example.wyyz.snapchat.db;

/**
 * Created by leify on 2016/10/13.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.DiscoveryChannel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by leify on 2016/10/12.
 */

public class DataBaseOperator {

    private Context mContext;


    public DataBaseOperator(Context mContext)
    {
        this.mContext = mContext;
    }

    public void initialise()
    {
        String html1 = "<h1>this is h1</h1>"
                + "<p>This text is normal</p>"
                + "<img src='http://pic.sc.chinaz.com/files/pic/pic9/201508/apic14052.jpg' />"
                + "<p>This is to test displaying the picture and text together \n here we will change another picture</p>"
                + "<p>change to another paragraph</p>"
                + "<img src='http://www.funnfun.in/wp-content/uploads/2013/09/cute-kitten-beautiful-picture-600x330.jpg' />"
                + "<p>test successful</p>"
                + "<img src='http://barkpost-assets.s3.amazonaws.com/wp-content/uploads/2013/11/grumpy-dog-11.jpg' />"
                + "<p>test successful</p>";

        String html2 = "<h1>this is h2</h1>"
                + "<p>This text is normal</p>"
                + "<img src='http://img11.deviantart.net/25cc/i/2014/358/7/3/fate_stay_night___goldenking_gilgamesh_wallpaper_2_by_ng9-d8b363i.jpg' />"
                + "<p>This is to test displaying the picture and text together \n here we will change another picture</p>"
                + "<p>change to another paragraph</p>"
                + "<img src='http://img.wallpaperfolder.com/f/7B8EA04DEA5B/fate-stay-night-unlimited-blade.png' />"
                + "<p>test successful</p>"
                + "<img src='http://cdn.wallpapersafari.com/61/24/x2Q7on.png' />"
                + "<p>test successful</p>";

        SnapChatOpenHelper dbHelper = new SnapChatOpenHelper(this.mContext, "snap_chat", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Channel", null, null, null, null, null, null);
//        Log.e("setting initialise","saved "+cursor.getCount());
        if(cursor.getCount()==0) {
            for(int i=0; i<10; i++) {
                ContentValues values = new ContentValues();
                values.put("channelId", i);
                values.put("name", "Discovery Channel "+i);
                values.put("visitNum", 0);
                values.put("subscriptionState", 0);

                Resources res = mContext.getResources();
                Bitmap bmp;
                if(i==0)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.number0);
                else if(i==1)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.number1);
                else if(i==2)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.number2);
                else if(i==3)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.number3);
                else if(i==4)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.number4);
                else if(i==5)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.number5);
                else if(i==6)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.number6);
                else if(i==7)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.number7);
                else if(i==8)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.number8);
                else
                    bmp = BitmapFactory.decodeResource(res, R.drawable.number9);

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
                byte[] profile = os.toByteArray();

                values.put("profile", profile);
                db.insert("Channel", null, values);
                values.clear();

                if(i%2==1) {
                    values.put("channelId", i);
                    values.put("content", html1);
                    db.insert("Content", null, values);
                    values.clear();

                    values.put("channelId", i);
                    values.put("content", html2);
                    db.insert("Content", null, values);
                    values.clear();
                }
                else
                {
                    values.put("channelId", i);
                    values.put("content", html2);
                    db.insert("Content", null, values);
                    values.clear();

                    values.put("channelId", i);
                    values.put("content", html1);
                    db.insert("Content", null, values);
                    values.clear();
                }

                Cursor cursorContent = db.query("Content", null, "channelId = "+i, null, null, null, null);
//                Log.e("content insert size",""+cursorContent.getCount());
                cursorContent.close();



            }
//            db.close();
        }
        cursor.close();
        db.close();
    }

    public int getChannelNum()
    {
        SnapChatOpenHelper dbHelper = new SnapChatOpenHelper(this.mContext, "snap_chat", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("Channel", null, null, null, null, null, null);

        int channelNum = cursor.getCount();
        cursor.close();
        db.close();
        return channelNum;
    }

    public void visit(int i)
    {
        SnapChatOpenHelper dbHelper = new SnapChatOpenHelper(this.mContext, "snap_chat", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("Channel", null, "channelId ="+i, null, null, null, null);
        int num =-1;
        if(cursor.moveToFirst())
            num = cursor.getInt(cursor.getColumnIndex("visitNum"))+1;
        cursor.close();;
        if(num!=-1) {

            ContentValues values = new ContentValues();
            values.put("visitNum", num);
            db.update("Channel", values, "channelId = ?", new String[]{""+i});
            db.close();
        }
    }

    public void subscribe(int i)
    {
        SnapChatOpenHelper dbHelper = new SnapChatOpenHelper(this.mContext, "snap_chat", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("Channel", null, "channelId ="+i, null, null, null, null);
        int num =-1;
        if(cursor.moveToFirst())
            num = cursor.getInt(cursor.getColumnIndex("subscriptionState"));
        cursor.close();;
        if(num==0) {

            ContentValues values = new ContentValues();
            values.put("subscriptionState", 1);
            db.update("Channel", values, "channelId = ?", new String[]{""+i});

        }
        db.close();
    }

    public void unsubscribe(int i)
    {
        SnapChatOpenHelper dbHelper = new SnapChatOpenHelper(this.mContext, "snap_chat", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("Channel", null, "channelId ="+i, null, null, null, null);
        int num =-1;
        if(cursor.moveToFirst())
            num = cursor.getInt(cursor.getColumnIndex("subscriptionState"));
        cursor.close();;
        if(num==1) {

            ContentValues values = new ContentValues();
            values.put("subscriptionState", 0);
            db.update("Channel", values, "channelId = ?", new String[]{""+i});
        }
        db.close();
    }

//    public String getEmail()
//    {
//
//    }

    public void update(String userEmail)
    {
        ArrayList<DiscoveryChannel> channels = getChannels();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        DatabaseReference channelRef = ref.child("DiscoveryChannel");

        for(int i=0;i<channels.size();i++)
        {
            channelRef = ref.child("DiscoveryChannel").child(""+i);

            Map<String, Object> updates = new HashMap<String, Object>();
            updates.put("channelId", channels.get(i).getChannelId());
            updates.put("channelNameId", "Discovery Channel "+i);

            Bitmap bmp = channels.get(i).getProfile();
            ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
            bmp.recycle();
            byte[] byteArray = bYtE.toByteArray();
            String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

            updates.put("profile", imageFile);
            channelRef.updateChildren(updates);
        }


        DatabaseReference contentRef = ref.child("DiscoveryContent");
        for(int i=0;i<channels.size();i++)
        {
            ArrayList<String> contents = channels.get(i).getContents();
            for(int a =0; a<contents.size(); a++) {
                contentRef = ref.child("DiscoveryContent").child("" + i+" "+a);

                Map<String, Object> updates = new HashMap<String, Object>();
                updates.put("channelId", channels.get(i).getChannelId());
                updates.put("content", contents.get(a));

                contentRef.updateChildren(updates);
            }
        }


        DatabaseReference recordRef = ref.child("DiscoveryRecord");
        for(int i=0;i<channels.size();i++)
        {
            recordRef = ref.child("DiscoveryRecord").child(""+i);

            Map<String, Object> updates = new HashMap<String, Object>();
            updates.put("channelId", channels.get(i).getChannelId());
            updates.put("userEmail", userEmail);

            updates.put("visitNum", channels.get(i).getVisitNum());
            updates.put("subscriptionState", channels.get(i).isSubscriptionState());
            recordRef.updateChildren(updates);
        }
    }

    public ArrayList<DiscoveryChannel> getChannels()
    {
        SnapChatOpenHelper dbHelper = new SnapChatOpenHelper(this.mContext, "snap_chat", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<DiscoveryChannel> channels = new ArrayList<DiscoveryChannel>();

        Cursor cursor = db.query("Channel", null, null, null, null, null, null);
        if(cursor.moveToFirst())
        {
            do{

                int channelId = cursor.getInt(cursor.getColumnIndex("channelId"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int visitNum = cursor.getInt(cursor.getColumnIndex("visitNum"));
                int subscriptionState = cursor.getInt(cursor.getColumnIndex("subscriptionState"));
                byte[] profile = cursor.getBlob(cursor.getColumnIndex("profile"));
                ArrayList<String> contents = new ArrayList<String>();

                Cursor cursorContent = db.query("Content", null, "channelId = "+channelId, null, null, null, null);
                if(cursorContent.moveToFirst())
                {
//                    Log.e("content size",""+cursorContent.getCount());
                    do{
                        String content = cursorContent.getString(cursorContent.getColumnIndex("content"));
                        contents.add(content);
                    }while(cursorContent.moveToNext());
                }

                DiscoveryChannel channel = new DiscoveryChannel(channelId, name, visitNum, subscriptionState, profile, contents);
                channels.add(channel);

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return channels;
    }
}
