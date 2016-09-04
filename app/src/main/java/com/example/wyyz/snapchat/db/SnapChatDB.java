package com.example.wyyz.snapchat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wyyz.snapchat.model.ChatRecord;
import com.example.wyyz.snapchat.model.Friends;
import com.example.wyyz.snapchat.model.MyStory;
import com.example.wyyz.snapchat.model.Snap;
import com.example.wyyz.snapchat.model.Story;
import com.example.wyyz.snapchat.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
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
            values.put("userName",user.getUserName());
            values.put("birthday", user.getBirthday().getTime());
            values.put("mobile", user.getMobile());
            values.put("avatar", user.getAvatar());
            values.put("QRcode", user.getQRcode());
            values.put("nickName",user.getNickName());
            db.insert("User", null, values);
        }
    }

    /**
     * Save a Friend relationship
     */
    public void saveFriendRelationship(Friends friends){
        if(friends!=null){
            ContentValues values = new ContentValues();
            values.put("ownerId", friends.getOwnerId());
            values.put("friendId", friends.getFriendId());
            values.put("createTime", friends.getCreateTime().getTime());
            values.put("editedName", friends.getEditedName());
            values.put("blocked",friends.isBlocked());
            values.put("lastChatTimeStamp", friends.getLastChatTimeStamp().getTime());
            db.insert("Friends", null, values);
        }
    }

    /**
     * Get friends list of a user
     */
    public List<Friends> getFriends(int userId){
        List<Friends> friends = new ArrayList<Friends>();
        String QUERY_FRIENDS="select * from Friends " +
                "where ownerId=?" +
                "order by lastChatTimeStamp";
        Cursor cursor = db.rawQuery(QUERY_FRIENDS, new String[]{String.valueOf(userId)});
        if(cursor.moveToFirst()){
            do{
                Friends friend=new Friends();
                friend.setOwnerId(cursor.getInt(cursor.getColumnIndex("ownerId")));
                friend.setFriendId(cursor.getInt(cursor.getColumnIndex("friendId")));
                friend.setBlocked(cursor.getInt(cursor.getColumnIndex("blocked"))>0);
                friend.setCreateTime(new Date(cursor.getLong(cursor.getColumnIndex("createTime"))));
                friend.setEditedName(cursor.getString(cursor.getColumnIndex("editedName")));
                friend.setLastChatTimeStamp(new Date(cursor.getLong(cursor.getColumnIndex("lastChatTimeStamp"))));
                Cursor sub_cursor = db.rawQuery("select userName, avatar, QRcode, nickName" +
                        " from User where id=?", new String[]{String.valueOf(friend.getOwnerId())});
                if(cursor.moveToFirst()){
                    friend.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
                    friend.setAvatar(cursor.getString(cursor.getColumnIndex("avatar")));
                    friend.setQRcode(cursor.getString(cursor.getColumnIndex("QRcode")));
                    friend.setNickName(cursor.getString(cursor.getColumnIndex("nickName")));
                }
                friends.add(friend);
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


}
