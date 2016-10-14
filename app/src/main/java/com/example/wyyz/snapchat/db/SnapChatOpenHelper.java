package com.example.wyyz.snapchat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SnapChatOpenHelper includes creating table statements,
 * constructor of setup database,
 * override onCreate() and onUpdate().
 * Created by ZIYUAN on 2/09/2016.
 */
public class SnapChatOpenHelper extends SQLiteOpenHelper{
    /**
     * User table creating statement
     */
    public static final String CREATE_USER = "create table User ( " +
            "id integer primary key autoincrement, " +
            "email text, " +
            "userName text, " +
            "birthday integer, " +
            "mobile text, " +
            "avatar text, " +
            "QRcode text)";

    /**
     * Friends table creating statement
     */
    public static final String CREATE_FRIENDS = "create table Friends ( " +
            "ownerId integer not null, " +
            "friendId integer not null, " +
            "createTime integer not null, " +
            "editedName text, " +
            "blocked integer, " +
            "lastChatTimeStamp integer not null," +
            "PRIMARY KEY(ownerId, friendId)," +
            "FOREIGN KEY(ownerId) REFERENCES User(id)," +
            "FOREIGN KEY(friendId) REFERENCES User(id))";

    /**
     * ChatRecord table creating statement
     */
    public static final String CREATE_CHATRECORD = "create table ChatRecord ( " +
            "id integer primary key autoincrement, " +
            "senderId integer not null," +
            "receiverId integer not null," +
            "snapId integer," +
            "storyId integer," +
            "content text," +
            "timeStamp integer not null," +
            "FOREIGN KEY(senderId) REFERENCES User(id)," +
            "FOREIGN KEY(receiverId) REFERENCES User(id))";

    /**
     * Snap table creating statement
     */
    public static final String CREATE_SNAP = "create table Snap (" +
            "id integer primary key autoincrement," +
            "userId integer not null," +
            "inMemory integer," +
            "path text," +
            "timingOut integer," +
            "timeStamp integer not null," +
            "location text," +
            "FOREIGN KEY(userId) REFERENCES User(id))";

    /**
     * Story table creating statement
     */
    public static final String CREATE_STORY = "create table Story ( " +
            "id integer primary key autoincrement," +
            "name text," +
            "timeStamp integer not null," +
            "locked integer)";

    /**
     * StorySnap table creating statement
     */
    public static final String CREATE_STORYSNAP = "create table StorySnap ( " +
            "snapId integer not null," +
            "storyId integer not null," +
            "PRIMARY KEY(snapId, storyId)," +
            "FOREIGN KEY(snapId) REFERENCES Snap(id)," +
            "FOREIGN KEY(storyId) REFERENCES Story(id))";

    /**
     * MyStory table creating statement
     */
    public static final String CREATE_MYSTORY = "create table MyStory ( " +
            "id integer primary key autoincrement," +
            "userId integer not null," +
            "storyId integer," +
            "snapId integer," +
            "timeStamp integer not null," +
            "public integer," +
            "snapNum integer," +
            "FOREIGN KEY(userId) REFERENCES User(id))";

    private static final String CREATE_DiscoveryChannel = "CREATE TABLE IF NOT EXISTS \"Channel\" (" +
            "  \"channelId\" int PRIMARY KEY NOT NULL," +
            "  \"name\" text NOT NULL," +
            "  \"visitNum\" int NOT NULL," +
            "  \"subscriptionState\" int NOT NULL," +
            "  \"profile\" blob NOT NULL);";

    private static final String CREATE_DiscoveryContent = "CREATE TABLE IF NOT EXISTS \"Content\" (" +
            "  \"channelId\" int NOT NULL," +
            "  \"content\" text NOT NULL" +
            ");";

    public SnapChatOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_FRIENDS);
        db.execSQL(CREATE_CHATRECORD);
        db.execSQL(CREATE_SNAP);
        db.execSQL(CREATE_STORY);
        db.execSQL(CREATE_STORYSNAP);
        db.execSQL(CREATE_MYSTORY);
        db.execSQL(CREATE_DiscoveryChannel);
        db.execSQL(CREATE_DiscoveryContent);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
