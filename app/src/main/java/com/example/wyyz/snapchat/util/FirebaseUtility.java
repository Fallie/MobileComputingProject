package com.example.wyyz.snapchat.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.activity.MyApplication;
import com.example.wyyz.snapchat.activity.MyfriendsActivity;
import com.example.wyyz.snapchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.LinkedList;


public class FirebaseUtility {

    private static final String TAG = FirebaseUtility.class.getSimpleName();
    private static DatabaseReference mFirebaseDatabaseReference;
    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseDatabase mFirebaseDatabase;
    private static FirebaseStorage mFirebaseStorage;
    private static StorageReference mStorageReference;

    public static void initialize(){
        enableDiskPersistance();
    }

    /**
     * get instance of FireBase Database
     *
     * @return
     */
    public static FirebaseDatabase getFireBaseDatabaseInstance() {
        if (mFirebaseDatabase == null) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
        }
        return mFirebaseDatabase;
    }

    /**
     * enable offline message
     */
    private static void enableDiskPersistance(){
        getFireBaseDatabaseInstance().setPersistenceEnabled(true);
    }

    /**
     * get FireBase Database Reference
     *
     * @return
     */
    public static DatabaseReference getFireBaseDatabaseReference() {
        if (mFirebaseDatabaseReference == null) {
            mFirebaseDatabaseReference = getFireBaseDatabaseInstance().getReference();
            mFirebaseDatabaseReference.keepSynced(true);
        }
        return mFirebaseDatabaseReference;
    }

    /**
     * get FireBase Database Chat Room Reference
     *
     * @return
     */
    public static DatabaseReference getFireBaseChatRoomDatabaseReference(){
        return getFireBaseDatabaseReference().child(AppConstants.CHATS_ROOT_NODE);
    }

    /**
     * get FireBase Database Chat Room Reference
     *
     * @return
     */
//    public static DatabaseReference getFireBaseUsersDatabaseReference(){
//        return getFireBaseDatabaseReference().child(AppConstants.USERS_ROOT_NODE);
//    }

    /**
     * get instance of FireBase Auth
     *
     * @return
     */
    public static FirebaseAuth getFireBaseAuthInstance() {
        if (mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return mFirebaseAuth;
    }

    /**
     * get current user for FireBase
     * @return
     */
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getCurrentUserEmail(){
        Log.d(TAG,"getCurrentUser+ "+ FirebaseUtility.getCurrentUser().getEmail());
        return FirebaseUtility.getCurrentUser() != null ? FirebaseUtility.getCurrentUser().getEmail() : "";
    }

    /**
     * get instance of FireBase Storage
     * @return
     */
    public static FirebaseStorage getFireBaseStorageInstance(){
        if(mFirebaseStorage == null){
            mFirebaseStorage = FirebaseStorage.getInstance();
        }
        return mFirebaseStorage;
    }

    /**
     * get FireBase Storage Reference
     * @return
     */
    public static StorageReference getFireBaseStorageReference(){
        if(mStorageReference == null){
            mStorageReference = getFireBaseStorageInstance().getReference();
        }
        return mStorageReference;
    }

    /**
     * query chats
     */
    public static void queryChatMessages(String selectedUserEmail){
        Query queryChatMessages = FirebaseUtility.getFireBaseChatRoomDatabaseReference()
                .child(FirebaseUtility.generateUniqueEmailID(selectedUserEmail))
                .orderByKey()
                .limitToLast(5);
        queryChatMessages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinkedList<Object> event = new LinkedList<Object>();
                event.add(AppConstants.MESSAGE_PREVIOUS_CHATS);
                event.add(dataSnapshot);
                MyApplication.getEventBusInstance().post(event);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * get reference of database to update message status
     * @param dataSnapshot
     * @return
     */
    public static DatabaseReference getDatabaseReferenceToUpdateMessageStatus(DataSnapshot dataSnapshot){
        return dataSnapshot.getRef();
    }

    /**
     * generates unique id for chatting between logged in user and other users
     * @param selectedUserEmail
     * @return
     */
    public static String generateUniqueEmailID(String selectedUserEmail){
        StringBuilder uniqueID = new StringBuilder();
        uniqueID.append(selectedUserEmail.replace("@","a").replace(".","z"))
                .append(FirebaseUtility.getCurrentUserEmail().replace("@","a").replace(".","z"));

        Log.d(TAG,"Unique "+ uniqueID.toString());

        /**
         * generate unique ID for chat, so that if two person chat it always remains unique between them
         */
        char[] charArray = uniqueID.toString().toCharArray();
        Arrays.sort(charArray);

        /**
         * clear StringBuilder and set the uniqueID
         */
        uniqueID.setLength(0);
        uniqueID.append(String.valueOf(charArray));

        return uniqueID.toString();
    }

//    /**
//     * set up presence logic
//     * @param userID
//     */
//    public static void updatePresenceOfCurrentUser(String userID){
//        final DatabaseReference myConnectionsRef = getFireBaseDatabaseReference().child("users/" + userID + "/connected").getRef();
//        final DatabaseReference lastOnlineRef = getFireBaseDatabaseReference().child("/users/" + userID + "/lastOnline").getRef();
//
//        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//        connectedRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                boolean connected = dataSnapshot.getValue(Boolean.class);
//                if (connected) {
//                    /**
//                     * when this device connects, set connected to 1 means true
//                     */
//                    myConnectionsRef.setValue(AppConstants.USER_ONLINE);
//
//                    // when this device disconnects, set connected to 0 means false
//                    myConnectionsRef.onDisconnect().setValue(AppConstants.USER_OFFLINE);
//
//                    // when I disconnect, update the last time I was seen online
//                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    /**
//     * set up Child Event Listener for FireBase Chat Messages
//     */
//    public static void addChatMessageListener(String uniqueID) {
//        ChildEventListener mChildEventListener = new ChildEventListener() {
//
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                LinkedList<Object> event = new LinkedList<Object>();
//                event.add(AppConstants.MESSAGE_ADDED);
//                event.add(dataSnapshot);
//
//                if(((MyApplication) MyApplication.getApplicationInstance().getApplicationContext()).isChatActivityInForeground()){
//                    MyApplication.getEventBusInstance().post(event);
//                }
//                else{
//                    sendNotification("New Message");
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                LinkedList<Object> event = new LinkedList<Object>();
//                event.add(AppConstants.MESSAGE_UPDATED);
//                event.add(dataSnapshot);
//                MyApplication.getEventBusInstance().post(event);
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                LinkedList<Object> event = new LinkedList<Object>();
//                event.add(AppConstants.MESSAGE_DELETED);
//                event.add(dataSnapshot);
//                MyApplication.getEventBusInstance().post(event);
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//
//        FirebaseUtility.getFireBaseChatRoomDatabaseReference().child(uniqueID).addChildEventListener(mChildEventListener);
//    }



//    /**
//     * query users
//     */
//    public static void queryUsersList(){
//        final Query queryUsersList = FirebaseUtility.getFireBaseUsersDatabaseReference()
//                .orderByKey();
//        queryUsersList.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                LinkedList<Object> event = new LinkedList<Object>();
//                event.add(AppConstants.USER_ADDED);
//                event.add(dataSnapshot);
//                MyApplication.getEventBusInstance().post(event);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }



    /**
     * send notification when message comes in background
     * @param messageBody
     */
//    private static void sendNotification(String messageBody) {
//        AppLog.e(TAG, "sendNotification "+messageBody);
//        Intent intent = new Intent(MyApplication.getApplicationInstance(), MyfriendsActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getApplicationInstance(), AppConstants.REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyApplication.getApplicationInstance())
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("New ChatMessage Message")
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent)
//                .setNumber(10);
//
//        NotificationManager notificationManager =
//                (NotificationManager) MyApplication.getApplicationInstance().getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(AppConstants.NOTIFICATION_ID, notificationBuilder.build());
//    }

//    /**
//     * save user details after successful login
//     * @param loggedInUserID
//     */
//    public static void saveUserDetails(String loggedInUserID){
//        FirebaseUtility.getFireBaseUsersDatabaseReference().child(loggedInUserID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.getValue() != null){
//                    User users = dataSnapshot.getValue(User.class);
//
//                    new AppPreferences(MyApplication.getApplicationInstance().getString(R.string.app_preferences), 1)
//                            .putString(MyApplication.getApplicationInstance().getString(R.string.pref_user_info), MyApplication.getGsonInstance().toJson(users).toString())
//                            .commit();
//
//                    new AppPreferences(MyApplication.getApplicationInstance().getString(R.string.app_preferences), 1)
//                            .putBoolean(MyApplication.getApplicationInstance().getString(R.string.pref_is_user_logged_in), true)
//                            .commit();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }


}
