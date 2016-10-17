package com.example.wyyz.snapchat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wyyz.snapchat.model.ChatRecord;
import com.example.wyyz.snapchat.model.Friend;
import com.example.wyyz.snapchat.model.MyStorySnap;
import com.example.wyyz.snapchat.model.Snap;
import com.example.wyyz.snapchat.model.Story;
import com.example.wyyz.snapchat.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    /**
     * Save a Friend relationship
     */
    public void saveFriendRelationship(Friend friend){
        if(friend!=null){
            ContentValues values = new ContentValues();
            values.put("ownerId", friend.getOwnerId());
            values.put("friendId", friend.getFriendId());
            values.put("createTime", friend.getCreateTime().getTime());
            values.put("editedName", friend.getEditedName());
            values.put("blocked",friend.isBlocked());
            values.put("lastChatTimeStamp", friend.getLastChatTimeStamp().getTime());
            db.insert("Friends", null, values);
        }
    }

    /**
     * Get friends list of a user
     */
    public List<Friend> getFriends(int userId){
        List<Friend> friends = new ArrayList<Friend>();
        String QUERY_FRIENDS="select * from Friends " +
                "where ownerId=?" +
                "order by lastChatTimeStamp desc";
        Cursor cursor = db.rawQuery(QUERY_FRIENDS, new String[]{String.valueOf(userId),"0"});
        if(cursor.moveToFirst()){
            do{
                Friend friend=new Friend();
                friend.setOwnerId(cursor.getInt(cursor.getColumnIndex("ownerId")));
                friend.setFriendId(cursor.getInt(cursor.getColumnIndex("friendId")));
                friend.setBlocked(cursor.getInt(cursor.getColumnIndex("blocked"))>0);
                friend.setCreateTime(new Date(cursor.getLong(cursor.getColumnIndex("createTime"))));
                friend.setEditedName(cursor.getString(cursor.getColumnIndex("editedName")));
                friend.setLastChatTimeStamp(new Date(cursor.getLong(cursor.getColumnIndex("lastChatTimeStamp"))));
                Cursor sub_cursor = db.rawQuery("select userName, avatar,QRcode" +
                        " from User where id=?", new String[]{String.valueOf(friend.getFriendId())});
                if(sub_cursor.moveToFirst()){
                    friend.setUserName(sub_cursor.getString(sub_cursor.getColumnIndex("userName")));
                    friend.setAvatar(sub_cursor.getString(sub_cursor.getColumnIndex("avatar")));
                    friend.setQRcode(sub_cursor.getString(sub_cursor.getColumnIndex("QRcode")));
                }
                friends.add(friend);
                sub_cursor.close();
            }while(cursor.moveToNext());
        }
        cursor.close();
        return friends;
    }

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
     * Save a story
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

    /**
     * Seed testing data
     */
   public void seedData(){
        User user1=new User();
        user1.setUsername("Ziyuan");
        user1.setEmail("ziyuan@gmail.com");
        saveUser(user1);
        User user2=new User();
        user2.setUsername("Linda");
        user2.setEmail("linda@gmail.com");
        saveUser(user2);
        User user3=new User();
        user3.setUsername("Alice");
        user3.setEmail("alice@gmail.com");
        saveUser(user3);
        User user4=new User();
        user4.setUsername("John");
        user4.setEmail("john@gmail.com");
        saveUser(user4);
        User user5=new User();
        user5.setUsername("Bob");
        user5.setEmail("bob@gmail.com");
        saveUser(user5);

        Friend friend1=new Friend();
        friend1.setOwnerId(findUserByEmail("ziyuan@gmail.com").getId());
        friend1.setFriendId(findUserByEmail("linda@gmail.com").getId());
        friend1.setCreateTime(new Date("Aug 21 2014 16:40:14"));
        friend1.setLastChatTimeStamp(new Date("Aug 21 2016 16:40:14"));
        saveFriendRelationship(friend1);

        Friend friend2=new Friend();
        friend2.setOwnerId(findUserByEmail("ziyuan@gmail.com").getId());
        friend2.setFriendId(findUserByEmail("alice@gmail.com").getId());
        friend2.setCreateTime(new Date("Aug 21 2015 16:40:14"));
        friend2.setLastChatTimeStamp(new Date("Aug 10 2016 16:40:14"));
        saveFriendRelationship(friend2);

        Friend friend3=new Friend();
        friend3.setOwnerId(findUserByEmail("ziyuan@gmail.com").getId());
        friend3.setFriendId(findUserByEmail("john@gmail.com").getId());
        friend3.setCreateTime(new Date("Sep 21 2014 16:40:14"));
        friend3.setLastChatTimeStamp(new Date("Aug 01 2016 16:40:14"));
        saveFriendRelationship(friend3);

        Friend friend4=new Friend();
        friend4.setOwnerId(findUserByEmail("ziyuan@gmail.com").getId());
        friend4.setFriendId(findUserByEmail("bob@gmail.com").getId());
        friend4.setCreateTime(new Date("Jan 21 2016 16:40:14"));
        friend4.setLastChatTimeStamp(new Date("Oct 21 2016 16:40:14"));
        saveFriendRelationship(friend4);

    }

}
