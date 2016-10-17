package com.example.wyyz.snapchat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wyyz.snapchat.model.ChatRecord;
import com.example.wyyz.snapchat.model.MyStorySnap;
import com.example.wyyz.snapchat.model.Snap;
import com.example.wyyz.snapchat.model.Story;
import com.example.wyyz.snapchat.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Setup database
 * Includes some basic options.
 * Created by ZIYUAN on 3/09/2016.
 */
public class SnapChatDB {
    /**
     * Database name
     */
    public static final String DB_NAME = "snap_chat";

    /**
     * Database version
     */
    public static final int VERSION = 1;

    private static SnapChatDB snapChatDB;

    private SQLiteDatabase db;

    private SnapChatDB(Context context){
        SnapChatOpenHelper dbHelper = new SnapChatOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * get SnapChatDB instance
     */
    public synchronized static SnapChatDB getInstance(Context context){
        if(snapChatDB == null){
            snapChatDB = new SnapChatDB(context);
        }
        return snapChatDB;
    }


    //Some basic database operations

    /**
     * Save a User instance into database
     */
    public void saveUser(User user){
        if(user!=null){
            ContentValues values=new ContentValues();
            values.put("email",user.getEmail());
            values.put("userName",user.getUsername());
            if(user.getBirthday()!=null) {
                values.put("birthday", user.getBirthday());
            }
            values.put("mobile", user.getMobile());
            values.put("avatar", user.getAvatar());
            values.put("QRcode", user.getQRcode());
            db.insert("User", null, values);
        }
    }

    /**
     * Find user by user name
     */
    public User findUserByEmail(String email){
        User user=null;
        String QUERY_FRIENDS="select * from User " +
                "where email=?";
        Cursor cursor = db.rawQuery(QUERY_FRIENDS, new String[]{String.valueOf(email)});
        if(cursor.moveToFirst()){
            user=new User();
            user.setAvatar(cursor.getString(cursor.getColumnIndex("avatar")));
            user.setBirthday(cursor.getString(cursor.getColumnIndex("birthday")));
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
            user.setQRcode(cursor.getString(cursor.getColumnIndex("QRcode")));
            user.setUsername(cursor.getString(cursor.getColumnIndex("userName")));
        }
        return user;
    }

    public void lockSnapByUri(String uri){
        ContentValues values = new ContentValues();
        values.put("isLocked", 1);
        db.update("Snap", values, "path=?",new String[]{uri});
    }

    //get user's locked snaps
    public ArrayList<Snap> getUserLockedSnaps(int userId){
        ArrayList<Snap> snaps = new ArrayList<Snap>();

        String QUERY_FRIENDS="select * from Snap " +
                "where userId=? AND " +
                "isLocked=?" +
                "order by timeStamp desc";
        Cursor cursor = db.rawQuery(QUERY_FRIENDS, new String[]{String.valueOf(userId),"1"});
        if(cursor.moveToFirst()){
            do{
                Snap snap=new Snap();
                snap.setUserId(cursor.getInt(cursor.getColumnIndex("userId")));
                snap.setChecked(false);
                snap.setPath(cursor.getString(cursor.getColumnIndex("path")));
                snap.setSize(cursor.getInt(cursor.getColumnIndex("size")));
                snap.setInMemory(cursor.getInt(cursor.getColumnIndex("inMemory"))>0);
                snap.setIsLocked(cursor.getInt(cursor.getColumnIndex("isLocked")));
                snap.setTimestamp(cursor.getString(cursor.getColumnIndex("timeStamp")));
                snap.setTimingOut(cursor.getInt(cursor.getColumnIndex("timingOut")));
                snaps.add(snap);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return snaps;
    }

    public void deleteSnapByPath(String path){

        db.delete("Snap","path=?",new String[]{path});
    }

    public void updateUsername(String email, String username){
        ContentValues values = new ContentValues();
        values.put("username", username);
        db.update("User", values, "email=?",new String[]{email});
    }

    public void updateUserMobile(String email, String mobile){
        ContentValues values = new ContentValues();
        values.put("mobile", mobile);
        db.update("User", values, "email=?",new String[]{email});
    }

    public void updateUserBirthday(String email,String birthday){
        ContentValues values = new ContentValues();
        values.put("birthday", birthday);
        db.update("User", values, "email=?",new String[]{email});
    }

    //get user's unlocked snaps
    public ArrayList<Snap> getUserSnap(int userId){
        ArrayList<Snap> snaps = new ArrayList<Snap>();
        String QUERY_FRIENDS="select * from Snap " +
                "where userId=? AND " +
                "isLocked=?" +
                "order by timeStamp desc";
        Cursor cursor = db.rawQuery(QUERY_FRIENDS, new String[]{String.valueOf(userId),"0"});
        if(cursor.moveToFirst()){
            do{
                Snap snap=new Snap();
                snap.setId(cursor.getInt(cursor.getColumnIndex("id")));
                snap.setUserId(cursor.getInt(cursor.getColumnIndex("userId")));
                snap.setChecked(false);
                snap.setPath(cursor.getString(cursor.getColumnIndex("path")));
                snap.setSize(cursor.getInt(cursor.getColumnIndex("size")));
                snap.setInMemory(cursor.getInt(cursor.getColumnIndex("inMemory"))>0);
                snap.setIsLocked(cursor.getInt(cursor.getColumnIndex("isLocked")));
                snap.setTimestamp(cursor.getString(cursor.getColumnIndex("timeStamp")));
                snap.setTimingOut(cursor.getInt(cursor.getColumnIndex("timingOut")));
                snaps.add(snap);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return snaps;
    }

    /**
     * Save a ChatRecord
     */
    public void saveChatRecord(ChatRecord record){
        if(record!=null){
            ContentValues values=new ContentValues();
            values.put("senderId",record.getSenderId());
            values.put("receiverId",record.getReceiverId());
            values.put("snapId",record.getSnapId());
            values.put("storyId",record.getStoryId());
            values.put("content", record.getContent());
            values.put("timeStamp",record.getTimestamp().getTime());
            db.insert("ChatRecord", null, values);
        }
    }

    /**
     * Save a Snap
     */
    public void Snap(Snap snap){
        if(snap!= null){
            ContentValues values=new ContentValues();
            values.put("userId", snap.getUserId());
            values.put("inMemory",snap.isInMemory());
            values.put("path", snap.getPath());
            values.put("size", snap.getSize());
            values.put("isLocked", snap.getIsLocked());
            values.put("timingOut", snap.getTimingOut());
            values.put("timeStamp", snap.getTimestamp());
            db.insert("Snap", null, values);
        }
    }

    /**
     * Create a story from a list of snaps
     */
    public void saveStory(User user, ArrayList<Snap> snaps){
        Story story=new Story();
        story.setTimestamp(new Date());
        story.setLocked(false);
        ContentValues values=new ContentValues();
        values.put("name", story.getName());
        values.put("timeStamp",story.getTimestamp().getTime());
        values.put("locked",story.isLocked());
        values.put("userId", user.getId());
        db.insert("Story", null, values);

        //add snap to story
        int storyId=getLastedStoryId();
        for(int i=0;i<snaps.size();i++){
            ContentValues storyValues=new ContentValues();
            storyValues.put("storyId",storyId);
            storyValues.put("snapId",snaps.get(i).getId());
            db.insert("StorySnap",null,storyValues);
        }
    }

    //get all the snaps of a story, time odered
    public ArrayList<Snap> getStorySnaps(Story story){
        ArrayList<Snap> snaps=new ArrayList<>();
        ArrayList<Integer> snapIds=new ArrayList<>();
        String QUERY="select snapId from StorySnap " +
                "where storyId=? " +
                "order by timeStamp";
        Cursor cursor = db.rawQuery(QUERY, new String[]{String.valueOf(story.getId())});
        if(cursor.moveToFirst()) {
            do {
                snapIds.add(cursor.getInt(cursor.getColumnIndex("snapId")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        for(int i=0;i<snapIds.size();i++){
            Snap snap=getSnapById(snapIds.get(i));
            snaps.add(snap);
        }
        return snaps;
    }

    //get the first snap of a story
    public Snap getStoryFirstSnap(Story story){
        int id=0;

        String QUERY="select snapId from StorySnap " +
                "where storyId=? " +
                "order by timeStamp " +
                "limit 1";
        Cursor cursor = db.rawQuery(QUERY, new String[]{String.valueOf(story.getId())});
        if(cursor.moveToFirst()){
            id=cursor.getInt(cursor.getColumnIndex("snapId"));
        }
        cursor.close();
        Snap snap=getSnapById(id);
        return snap;
    }
    //get a user's stories
    public ArrayList<Story> getUserStories(int userId){
        ArrayList<Story> stories = new ArrayList<Story>();
        String QUERY="select * from Story " +
                "where userId=? AND " +
                "locked=?" +
                "order by timeStamp desc";
        Cursor cursor = db.rawQuery(QUERY, new String[]{String.valueOf(userId),"0"});
        if(cursor.moveToFirst()){
            do{
                Story story=new Story();
                story.setLocked(cursor.getInt(cursor.getColumnIndex("locked"))>0);
                story.setTimestamp(new Date(cursor.getInt(cursor.getColumnIndex("timeStamp"))));
                story.setId(cursor.getInt(cursor.getColumnIndex("id")));
                story.setName(cursor.getString(cursor.getColumnIndex("name")));
                stories.add(story);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return stories;
    }

    //get a snap by id
    public Snap getSnapById(int id){
        Snap snap=null;
        String QUERY="select * from Snap " +
                "where id=?";
        Cursor cursor = db.rawQuery(QUERY, new String[]{String.valueOf(id)});
        if(cursor.moveToFirst()){
            snap=new Snap();
            snap.setUserId(cursor.getInt(cursor.getColumnIndex("userId")));
            snap.setChecked(false);
            snap.setPath(cursor.getString(cursor.getColumnIndex("path")));
            snap.setSize(cursor.getInt(cursor.getColumnIndex("size")));
            snap.setInMemory(cursor.getInt(cursor.getColumnIndex("inMemory"))>0);
            snap.setIsLocked(cursor.getInt(cursor.getColumnIndex("isLocked")));
            snap.setTimestamp(cursor.getString(cursor.getColumnIndex("timeStamp")));
            snap.setTimingOut(cursor.getInt(cursor.getColumnIndex("timingOut")));
        }
        cursor.close();
        return snap;
    }

    private int getLastedStoryId(){
        int id = 0;
        String QUERY="select id from Story order by timeStamp desc " +
                "limit 1";
        Cursor cursor = db.rawQuery(QUERY, null);
        if(cursor.moveToFirst()){
            id=cursor.getInt(cursor.getColumnIndex("id"));
        }
        cursor.close();
        return id;
    }

    /**
     * Save a Mystory
     */
    public void saveMyStory(MyStorySnap myStorySnap, String userId){
        ContentValues values=new ContentValues();
        values.put("userId", userId);
        values.put("timeStamp", new SimpleDateFormat("yyyyMMdd_HHmmss").format(myStorySnap.getTimestamp()));
        values.put("timingout", myStorySnap.getTimingOut());
        values.put("url", myStorySnap.getPath());
        db.insert("MyStory", null, values);
        values.clear();
    }

    public ArrayList<MyStorySnap> getMyStory(String userId){
        ArrayList<MyStorySnap> myStory = new ArrayList<MyStorySnap>();
        Cursor cursor = db.query("MyStory", null, "userId = \""+userId+"\"", null, null, null, null);
        if(cursor.moveToFirst())
        {
            do {
                MyStorySnap snap = new MyStorySnap();
                snap.setPath(cursor.getString(cursor.getColumnIndex("url")));
                snap.setTimestamp(cursor.getString(cursor.getColumnIndex("timeStamp")));
                snap.setTimingOut(cursor.getInt(cursor.getColumnIndex("timingout")));
                myStory.add(snap);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return myStory;
    }

}
