package com.example.wyyz.snapchat.util;

public class AppConstants {

    public static final String CHATS_ROOT_NODE = "chats";
    public static final String USERS_ROOT_NODE = "users";
    public static final String STORAGE_FOLDER_NAME = "images";
    public static final String INTENT_GROUP_SELECTED_GROUP = "INTENT_GROUP_SELECTED_GROUP";

    /**
     * Users Presence
     */
    public static final int USER_ONLINE = 1;
    public static final int USER_OFFLINE = 0;

    /**
     * Chat Message Delivery
     */
    public static final int MESSAGE_DELIVERED_TO_SERVER = 0;
    public static final int MESSAGE_DELIVERED_TO_USER = 1;
    public static final int MESSAGE_SEEN_BY_USER = 2;

    /**
     * Chat Message Types
     */
    public static final int MESSAGE_ADDED = 200;
    public static final int MESSAGE_UPDATED = 201;
    public static final int MESSAGE_DELETED = 202;
    public static final int MESSAGE_PREVIOUS_CHATS = 203;

    public static final int USER_ADDED = 300;

    public static final int NOTIFICATION_ID = 100;
    public static final int REQUEST_CODE = 101;
}
