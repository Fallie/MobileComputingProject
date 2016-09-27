package com.example.wyyz.snapchat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wyyz.snapchat.model.ChatRecord;
import com.example.wyyz.snapchat.model.Friend;
import com.example.wyyz.snapchat.model.MyStory;
import com.example.wyyz.snapchat.model.Snap;
import com.example.wyyz.snapchat.model.Story;
import com.example.wyyz.snapchat.model.User;

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
            values.put("QRcode", user.getQRcode().toString());
            db.insert("User", null, values);
        }
    }

    /**
     * Find user by user name
     */
    public User findUserByUsername(String username){
        User user=null;
        String QUERY_FRIENDS="select * from User " +
                "where userName=?";
        Cursor cursor = db.rawQuery(QUERY_FRIENDS, new String[]{String.valueOf(username)});
        if(cursor.moveToFirst()){
            user=new User();
            user.setAvatar(cursor.getString(cursor.getColumnIndex("avatar")));
            user.setBirthday(cursor.getString(cursor.getColumnIndex("birthday")));
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
            user.setQRcode(cursor.getString(cursor.getColumnIndex("QRcode")));
        }
        return user;
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
        Cursor cursor = db.rawQuery(QUERY_FRIENDS, new String[]{String.valueOf(userId)});
        if(cursor.moveToFirst()){
            do{
                Friend friend=new Friend();
                friend.setOwnerId(cursor.getInt(cursor.getColumnIndex("ownerId")));
                friend.setFriendId(cursor.getInt(cursor.getColumnIndex("friendId")));
                friend.setBlocked(cursor.getInt(cursor.getColumnIndex("blocked"))>0);
                friend.setCreateTime(new Date(cursor.getLong(cursor.getColumnIndex("createTime"))));
                friend.setEditedName(cursor.getString(cursor.getColumnIndex("editedName")));
                friend.setLastChatTimeStamp(new Date(cursor.getLong(cursor.getColumnIndex("lastChatTimeStamp"))));
                Cursor sub_cursor = db.rawQuery("select userName, avatar, QRcode, nickName" +
                        " from User where id=?", new String[]{String.valueOf(friend.getFriendId())});
                if(sub_cursor.moveToFirst()){
                    friend.setUserName(sub_cursor.getString(sub_cursor.getColumnIndex("userName")));
                    friend.setAvatar(sub_cursor.getString(sub_cursor.getColumnIndex("avatar")));
                    friend.setQRcode(sub_cursor.getString(sub_cursor.getColumnIndex("QRcode")));
                    friend.setNickName(sub_cursor.getString(sub_cursor.getColumnIndex("nickName")));
                }
                friends.add(friend);
                sub_cursor.close();
            }while(cursor.moveToNext());
        }
        cursor.close();
        return friends;
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
            values.put("timingOut", snap.getTimingOut());
            values.put("timeStamp", snap.getTimestamp().getTime());
            values.put("location",snap.getLocation());
            db.insert("Snap", null, values);
        }
    }

    /**
     * Save a story
     */
    public void saveStory(Story story){
        ContentValues values=new ContentValues();
        values.put("name", story.getName());
        values.put("timeStamp",story.getTimestamp().getTime());
        values.put("locked",story.isLocked());
        db.insert("Story", null, values);
    }

    /**
     * Save a Mystory
     */
    public void saveMyStory(MyStory myStory){
        ContentValues values=new ContentValues();
        values.put("userId", myStory.getUserId());
        values.put("storyId",myStory.getStoryId());
        values.put("snapId", myStory.getSnapId());
        values.put("timeStamp", myStory.getTimestamp().getTime());
        values.put("public", myStory.isWhetherPublic());
        values.put("snapNum", myStory.getSnapNum());
        db.insert("MyStory", null, values);
    }

    /**
     * Seed testing data
     */
    public void seedData(){
        User user1=new User();
        user1.setUsername("Ziyuan");
        saveUser(user1);
        User user2=new User();
        user2.setUsername("Linda");
        saveUser(user2);
        User user3=new User();
        user3.setUsername("Alice");
        saveUser(user3);
        User user4=new User();
        user4.setUsername("John");
        saveUser(user4);
        User user5=new User();
        user5.setUsername("Bob");
        saveUser(user5);

        Friend friend1=new Friend();
        friend1.setOwnerId(findUserByUsername("ziyuan_w").getId());
        friend1.setFriendId(findUserByUsername("linda_a").getId());
        friend1.setCreateTime(new Date("Aug 21 2014 16:40:14"));
        friend1.setLastChatTimeStamp(new Date("Aug 21 2016 16:40:14"));
        saveFriendRelationship(friend1);

        Friend friend2=new Friend();
        friend2.setOwnerId(findUserByUsername("ziyuan_w").getId());
        friend2.setFriendId(findUserByUsername("alice_b").getId());
        friend2.setCreateTime(new Date("Aug 21 2015 16:40:14"));
        friend2.setLastChatTimeStamp(new Date("Aug 10 2016 16:40:14"));
        saveFriendRelationship(friend2);

        Friend friend3=new Friend();
        friend3.setOwnerId(findUserByUsername("ziyuan_w").getId());
        friend3.setFriendId(findUserByUsername("john_c").getId());
        friend3.setCreateTime(new Date("Sep 21 2014 16:40:14"));
        friend3.setLastChatTimeStamp(new Date("Aug 01 2016 16:40:14"));
        saveFriendRelationship(friend3);

        Friend friend4=new Friend();
        friend4.setOwnerId(findUserByUsername("ziyuan_w").getId());
        friend4.setFriendId(findUserByUsername("bob_d").getId());
        friend4.setCreateTime(new Date("Jan 21 2016 16:40:14"));
        friend4.setLastChatTimeStamp(new Date("Oct 21 2016 16:40:14"));
        saveFriendRelationship(friend4);

    }

}
