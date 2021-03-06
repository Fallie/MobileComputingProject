package com.example.wyyz.snapchat.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wyyz.snapchat.Interface.CustomClickImageListener;
import com.example.wyyz.snapchat.Interface.CustomOnItemClickListener;
import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.activity.chat.AbsCommomAdapter;
import com.example.wyyz.snapchat.model.ChatMessage;
import com.example.wyyz.snapchat.model.FileModel;
import com.example.wyyz.snapchat.util.AppConstants;

import com.example.wyyz.snapchat.util.ConnectionDetector;
import com.example.wyyz.snapchat.util.FirebaseUtility;
import com.example.wyyz.snapchat.util.ShowNetworkAlert;
import com.example.wyyz.snapchat.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonElement;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.example.wyyz.snapchat.util.AppConstants.FILE_URL;

/**
 * The activity is mainly used for chatting between two friends. Friends can talk each other, send pictures through gallery
 * or mobile camera. By tapping at the picture it can be displayed on full screen. Meanwhile, there is an additional limitation
 * that the chat will only keep the latest 5 chats.
 */

public class ChatActivity extends BaseActivity implements CustomOnItemClickListener, View.OnClickListener,CustomClickImageListener{

    private static final String TAG = ChatActivity.class.getSimpleName();
    private RecyclerView recyclerViewChat;
    private TextView textViewNoConversations;
    private Button buttonSendMessage,buttonSendPic;
    private EditText editTextSendMessage;
    private ChatMessageAdapter chatMessageAdapter;
    private Toolbar toolbar;
    private String selectedUserID;
    private String selectedUsername;
    private String fileUrl;
    private File filePathImageCamera;
    private String urlPhotoClick;
    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;

    // Connection detector class
    private ConnectionDetector cd;
    // flag for Internet connection status
    private Boolean isInternetPresent = false;
    // Alert Dialog Manager
    private ShowNetworkAlert alert = new ShowNetworkAlert();

    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppTheme();
        super.onCreate(savedInstanceState);

        cd = new ConnectionDetector(getApplicationContext());
        checkavailability();

        setContentView(R.layout.activity_chat);

        getIntentExtras();
        initComponents();
        addListeners();
        setUpToolbar();
        Log.i(TAG, selectedUsername +" username");

        if (fileUrl!=null){

            StorageReference storageRef = storage.getReferenceFromUrl(Util.URL_STORAGE_REFERENCE).child(Util.FOLDER_STORAGE_IMG);

            //Uri selectedImageUri = Uri.parse(fileUrl);

            //sendMessage(FirebaseUtility.getCurrentUser().getEmail(),null,storageRef,selectedImageUri,"");
            sendsanpMessage(FirebaseUtility.getCurrentUserEmail(),null,fileUrl,storageRef);

            //File filea = new File(fileUrl);

            //sendMessage(FirebaseUtility.getCurrentUser().getEmail(),"", filea ,storageRef);

            fileUrl = null;

        }
    }

    public void checkavailability() {
        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(ChatActivity.this,
                    "Fail",
                    "Internet Connection is NOT Available", false);
            // stop executing code by return
            return;
        }

    }
    private void getIntentExtras() {

        selectedUserID = getIntent().getStringExtra(AppConstants.INTENT_GROUP_SELECTED_GROUP);
        selectedUsername = getIntent().getStringExtra("user_name");
        Log.v(TAG,selectedUserID);
        if(getIntent().getStringExtra(FILE_URL)!= null){
            fileUrl = getIntent().getStringExtra(FILE_URL);
            Log.v(TAG, "in url "+ fileUrl);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        if(!MyApplication.getEventBusInstance().isRegistered(this)){
            MyApplication.getEventBusInstance().register(this);
        }
        ((MyApplication)getApplication()).setChatActivityInForeground(true);
    }

    @Override
    void initComponents() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        buttonSendMessage = (Button) findViewById(R.id.buttonSendMessage);
        buttonSendPic = (Button)  findViewById(R.id.buttonSendPic);
        editTextSendMessage = (EditText) findViewById(R.id.editTextSendMessage);

        textViewNoConversations = (TextView) findViewById(R.id.textViewNoConversations);

        recyclerViewChat = (RecyclerView) findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewChat.setLayoutManager(mLinearLayoutManager);
    }

    @Override
    void addListeners() {
        setRecyclerViewAdapter();
        buttonSendMessage.setOnClickListener(this);
        buttonSendPic.setOnClickListener(this);
    }

    private void setRecyclerViewAdapter(){
        chatMessageAdapter = new ChatMessageAdapter(this, selectedUserID, textViewNoConversations, recyclerViewChat);
        recyclerViewChat.setAdapter(chatMessageAdapter);
    }

    private void setUpToolbar(){
        if(toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onImageClick() {


        Log.d(TAG,urlPhotoClick+" Testout");
        Intent intent = new Intent(ChatActivity.this,DisplaySnapActivity.class);
        ArrayList<String> str = new ArrayList<String>();
        str.add(Uri.parse(urlPhotoClick).toString());
        int[] timer = {3};

        intent.putStringArrayListExtra("SnapPath",str);
        intent.putExtra("Timer",timer);
        intent.putExtra("ActivityName","ChatActivity");
        intent.putExtra("UserName", selectedUserID);
        intent.putExtra("user_name", selectedUsername);

        Log.d(TAG, "Ready to display "+ selectedUserID);
        startActivity(intent);
    }
    /**
     * send message to FireBase Database
     * @param sender
     * @param message
     */
    private void sendMessage(final String sender,final String message, final File file ,StorageReference storageReference) {
        if (file != null){
            if (storageReference != null){
                UploadTask uploadTask = storageReference.putFile(Uri.fromFile(file));
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"onFailure sendFileFirebase "+e.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG,"onSuccess sendFileFirebase");
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        FileModel fileModel = new FileModel("img",downloadUrl.toString(),file.getName(),file.length()+"");
                        ChatMessage newChatMessage = new ChatMessage(System.currentTimeMillis(), sender, message, 0, fileModel);
                        FirebaseUtility.getFireBaseChatRoomDatabaseReference().child(FirebaseUtility.generateUniqueEmailID(selectedUserID)).push().setValue(newChatMessage, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.d(TAG, "Data could not be saved. " + databaseError.getMessage());
                                } else {
                                    chatMessageAdapter.updateMessageStatus(databaseReference, 1);
                                    Log.d(TAG, "Data saved successfully.");
                                    FirebaseUtility.queryChatMessagesFresh(selectedUserID);
                                }
                            }
                        });
                    }
                });
            }
        }else {
            ChatMessage newChatMessage = new ChatMessage(System.currentTimeMillis(), sender, message, 0, null);

            FirebaseUtility.getFireBaseChatRoomDatabaseReference().child(FirebaseUtility.generateUniqueEmailID(selectedUserID)).push().setValue(newChatMessage, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d(TAG, "Data could not be saved. " + databaseError.getMessage());
                    } else {
                        chatMessageAdapter.updateMessageStatus(databaseReference, 1);
                        Log.d(TAG, "Data saved successfully.");
                        FirebaseUtility.queryChatMessagesFresh(selectedUserID);
                    }
                }
            });
        }
    }



    @Override
    public void onItemClick(View v, int position, Object value) {
        Log.e(TAG, "Item Clicked at position - " + position + " and value - " + value);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSendMessage:
                if (TextUtils.isEmpty(editTextSendMessage.getText().toString().trim())) {
                    showInputFieldError(editTextSendMessage, getString(R.string.enter_chat_message));
                } else {
                    sendMessage(FirebaseUtility.getCurrentUser().getEmail(), editTextSendMessage.getText().toString(), null, null);
                    editTextSendMessage.setText("");
                }
                break;
            case R.id.buttonSendPic:
//                Toast.makeText(v.getContext(),"pic",Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(v.getContext(),MainActivity.class);
//                startActivityForResult(i,IMAGE_CAMERA_REQUEST);
                functionDialog();
                break;
        }

    }

    private void functionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        List<String> list = new ArrayList<>();
        list.add("camera");
        list.add("photo");
        builder.setAdapter(new AbsCommomAdapter<String>(this, list, R.layout.layout_cell) {

                               @Override
                               protected void injectData(final ChViewHolder holder, String s) {
                                   holder.setTextViewText(R.id.cell_text1, s);
                               }
                           },
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0 ){
                            photoCameraIntent();
                        }else {
                            photoGalleryIntent();
                        }
                    }
                });
        builder.setCancelable(true);
        builder.show();
    }
    @Subscribe
    public void onChatEvent(LinkedList<Object> event){

        int eventType = (int) event.get(0);
        DataSnapshot dataSnapshot = (DataSnapshot) event.get(1);

        if(chatMessageAdapter == null){
            setRecyclerViewAdapter();
        }

        switch (eventType){
            case AppConstants.MESSAGE_ADDED:
                chatMessageAdapter.messageAdded(dataSnapshot);
                break;
            case AppConstants.MESSAGE_UPDATED:
                chatMessageAdapter.messageUpdated(dataSnapshot);

                break;
            case AppConstants.MESSAGE_DELETED:
                chatMessageAdapter.messageDeleted(dataSnapshot);
                break;
            case AppConstants.MESSAGE_PREVIOUS_CHATS:
                chatMessageAdapter.messagePreviousChats(dataSnapshot);
                break;
        }
    }


    private class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessagesViewHolder> {

        private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
        private CustomOnItemClickListener customOnItemClickListener;
        private Context context;
        private String selectedUserID;
        private TextView textViewNoConversations;
        private RecyclerView recyclerViewChat;

        public ChatMessageAdapter(Context context, String selectedUserID, TextView textViewNoConversations, RecyclerView recyclerViewChat) {
            this.context = context;
//            this.selectedUserUUID = selectedUserUUID;
            this.selectedUserID = selectedUserID;

            this.textViewNoConversations = textViewNoConversations;
            this.recyclerViewChat = recyclerViewChat;
            this.customOnItemClickListener = (CustomOnItemClickListener) context;
            /**
             * get previous chats
             */
            FirebaseUtility.queryChatMessages(this.selectedUserID);
        }

        private void messageAdded(DataSnapshot dataSnapshot){
            if (dataSnapshot.getValue() != null) {
                ChatMessage chat = dataSnapshot.getValue(ChatMessage.class);
                Log.e(TAG, chat.getSender() + " - " + chat.getMessage());
                chatMessageList.add(chat);
                notifyItemInserted(chatMessageList.size() - 1);
                recyclerViewChat.scrollToPosition(chatMessageList.size() - 1);

                Log.e(TAG, dataSnapshot.getKey()+" - addChildEventListener:onChildAdded - " + dataSnapshot.getValue().toString()+" MessageStatus - "+chat.getMessageStatus());

                /**
                 * if message is delivered to server, then only update it to delivered to user
                 */
                if(chat.getMessageStatus() == 1){
                    updateMessageStatus(FirebaseUtility.getDatabaseReferenceToUpdateMessageStatus(dataSnapshot), 2);
                }

                if(((MyApplication)((ChatActivity)(context)).getApplication()).isChatActivityInForeground()){
                    /**
                     * if message is delivered to server, then only update it to seen by user
                     */
                    if(chat.getMessageStatus() == 2){
                        updateMessageStatus(FirebaseUtility.getDatabaseReferenceToUpdateMessageStatus(dataSnapshot), 3);
                    }
                }
            }
        }

        private void messageUpdated(DataSnapshot dataSnapshot){
            if (dataSnapshot.getValue() != null) {

                ChatMessage updatedChat = dataSnapshot.getValue(ChatMessage.class);

                int chatIndex = chatMessageList.indexOf(updatedChat);

                if (chatIndex > -1) {
                    chatMessageList.set(chatIndex, updatedChat);
                    notifyItemChanged(chatIndex);

                    /**
                     * if message is delivered to server, then only update it to delivered to user
                     */
                    if(updatedChat.getMessageStatus() == 1){
                        updateMessageStatus(FirebaseUtility.getDatabaseReferenceToUpdateMessageStatus(dataSnapshot), 2);
                    }

                    if(((MyApplication)((ChatActivity)(context)).getApplication()).isChatActivityInForeground()){
                        /**
                         * if message is delivered to server, then only update it to seen by user
                         */
                        if(updatedChat.getMessageStatus() == 2){
                            updateMessageStatus(FirebaseUtility.getDatabaseReferenceToUpdateMessageStatus(dataSnapshot), 3);
                        }
                    }
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + updatedChat);
                }
            }
        }

        private void messageDeleted(DataSnapshot dataSnapshot){
            if (dataSnapshot.getValue() != null) {

                ChatMessage removedChat = dataSnapshot.getValue(ChatMessage.class);

                int chatIndex = chatMessageList.indexOf(removedChat);
                if (chatIndex > -1) {
                    chatMessageList.remove(chatIndex);
                    notifyItemRemoved(chatIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + removedChat);
                }
            }
        }

        private void messagePreviousChats(DataSnapshot dataSnapshot){
            if (dataSnapshot.getValue() != null) {

                HashMap<String, Object> chatMessageNode = (HashMap<String, Object>) dataSnapshot.getValue();

                for (Map.Entry<String, Object> chatMessageEntry : chatMessageNode.entrySet()) {

                    JsonElement jsonElement = MyApplication.getGsonInstance().toJsonTree(chatMessageEntry.getValue());
                    ChatMessage previousChatMessage = MyApplication.getGsonInstance().fromJson(jsonElement, ChatMessage.class);

                    Log.e(TAG, previousChatMessage.getSender() + " - " + previousChatMessage.getMessage());
                    chatMessageList.add(previousChatMessage);

                    /**
                     * sort in ascending order using Comparable
                     */
                    Collections.sort(chatMessageList);

                    notifyItemInserted(chatMessageList.size() - 1);
                    recyclerViewChat.scrollToPosition(chatMessageList.size() - 1);
                }
            }
        }

        /**
         * update message delivery status
         * @param databaseReference
         */
        private void updateMessageStatus(DatabaseReference databaseReference, int messageStatus){
            HashMap<String, Object> updateMessageStatusMap = new HashMap<String, Object>();
            updateMessageStatusMap.put("messageStatus", messageStatus);
            databaseReference.updateChildren(updateMessageStatusMap);
        }

        @Override
        public ChatMessageAdapter.MessagesViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

            View view = null;
            ChatMessageAdapter.MessagesViewHolder messagesViewHolder = null;

            switch (viewType){
                case 0:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_right, parent, false);
                    messagesViewHolder = new ChatMessageAdapter.MessagesViewHolder(view, viewType);
                    break;
                case 1:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_left, parent, false);
                    messagesViewHolder = new ChatMessageAdapter.MessagesViewHolder(view, viewType);
                    break;
            }

            final MessagesViewHolder finalMessagesViewHolder = messagesViewHolder;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customOnItemClickListener.onItemClick(v, finalMessagesViewHolder.getAdapterPosition(), chatMessageList.get(finalMessagesViewHolder.getAdapterPosition()));
                }
            });

            Log.e(ChatMessageAdapter.class.getSimpleName(), "onCreateViewHolder");
            return messagesViewHolder;
        }

        @Override
        public void onBindViewHolder(ChatMessageAdapter.MessagesViewHolder messagesViewHolder, int position) {
            Log.e(ChatMessageAdapter.class.getSimpleName(), "onBindViewHolder - " + position);

            switch (getItemViewType(position)){
                case 0:
                    displayChat(messagesViewHolder, position);
                    break;
                case 1:
                    displayChat(messagesViewHolder, position);
                    break;
            }
        }

        private void displayChat(MessagesViewHolder messagesViewHolder, int position){
            Log.d(TAG,"Sender "+ chatMessageList.get(position).getSender());


            String senderEmail = chatMessageList.get(position).getSender();

            Log.d(TAG, senderEmail+ "sender");
            //User sender = SnapChatDB.getInstance(context).findUserByEmail(chatMessageList.get(position).getSender());
            //Log.d(TAG, "Username "+sender.getUsername());

//            if (sender.getUsername()!=null){
//                messagesViewHolder.textViewSender.setText(SnapChatDB.getInstance(context).findUserByEmail(chatMessageList.get(position).getSender()).getUsername());
//=======
            // String uname = SnapChatDB.getInstance(context).findUserByEmail(chatMessageList.get(position).getSender())
            //  .getUsername();
            //if (uname!=null){

            Log.d(TAG, selectedUsername +" username");
            messagesViewHolder.textViewSender.setText(selectedUsername);

            if (chatMessageList.get(position).getMessage() != null ) {
                messagesViewHolder.textViewMessage.setText(chatMessageList.get(position).getMessage());
                messagesViewHolder.textViewMessage.setVisibility(View.VISIBLE);
                messagesViewHolder.imageView.setVisibility(View.GONE);
            }else {
                messagesViewHolder.textViewMessage.setVisibility(View.GONE);
                messagesViewHolder.imageView.setVisibility(View.VISIBLE);
                if (chatMessageList.get(position).getFile() != null)
                    messagesViewHolder.setIvChatPhoto(chatMessageList.get(position).getFile().getUrl_file());
            }


            messagesViewHolder.textViewTime.setText(MyApplication.getSimpleDateFormat().format(chatMessageList.get(position).getCurrentTime()));
            Log.d(TAG," (Message Status - "+ chatMessageList.get(position).getMessageStatus()+")");
            //}

        }

        @Override
        public int getItemViewType(int position) {
            if(FirebaseUtility.getCurrentUser().getEmail().equalsIgnoreCase(chatMessageList.get(position).getSender())){
                return 0;
            }
            else{
                return 1;
            }
        }

        @Override
        public int getItemCount() {
            textViewNoConversations.setVisibility(chatMessageList.size() > 0 ? View.GONE : View.VISIBLE);
            return chatMessageList.size();
        }

        public class MessagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView textViewSender, textViewMessage, textViewTime;
            private ImageView imageView;

            private CustomClickImageListener customClickImageListener;


            public MessagesViewHolder(View view, int viewType) {
                super(view);

                switch (viewType){
                    case 0:
                        textViewSender = (TextView) view.findViewById(R.id.textViewSender);
                        textViewMessage = (TextView) view.findViewById(R.id.textViewMessage);
                        textViewTime = (TextView) view.findViewById(R.id.textViewTime);
                        imageView = (ImageView) view.findViewById(R.id.ImageViewMessage);
                        break;
                    case 1:
                        textViewSender = (TextView) view.findViewById(R.id.textViewSender);
                        textViewMessage = (TextView) view.findViewById(R.id.textViewMessage);
                        textViewTime = (TextView) view.findViewById(R.id.textViewTime);
                        imageView = (ImageView) view.findViewById(R.id.ImageViewMessage);
                        break;
                }
            }

            public void setIvChatPhoto(String url){
                if (imageView == null)return;
                Glide.with(imageView.getContext()).load(url)
                        .override(200, 200)
                        // .fitCenter()
                        .into(imageView);

                imageView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                int position = getAdapterPosition();
                Log.d(TAG,"HI "+position);

                String username = chatMessageList.get(position).getSender();
                Log.e("TAG", "username "+username);
                String imageurl = chatMessageList.get(position).getFile().getUrl_file();
                Log.e("TAG", "imageurl "+ imageurl);

                urlPhotoClick = imageurl;

                onImageClick();

            }
        }
    }

    private void photoCameraIntent(){
        String filename = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filename+"camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(filePathImageCamera));
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }


    private void photoGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }
    @Override
    protected void onPause() {
        super.onPause();

        ((MyApplication)getApplication()).setChatActivityInForeground(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        chatMessageAdapter.removeEventListener();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        StorageReference storageRef = storage.getReferenceFromUrl(Util.URL_STORAGE_REFERENCE).child(Util.FOLDER_STORAGE_IMG);

        if (requestCode == IMAGE_GALLERY_REQUEST){
            if (resultCode == RESULT_OK){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    sendMessage(FirebaseUtility.getCurrentUser().getEmail(),null,storageRef,selectedImageUri,"");
                }else{
                    //URI IS NULL
                }
            }
        }else
        if (requestCode == IMAGE_CAMERA_REQUEST){
            if (resultCode == RESULT_OK){
                if (filePathImageCamera != null && filePathImageCamera.exists()){
                    StorageReference imageCameraRef = storageRef.child(filePathImageCamera.getName()+"_camera");
                    sendMessage(FirebaseUtility.getCurrentUser().getEmail(),null,filePathImageCamera,imageCameraRef);
                }else{
                    //IS NULL
                }
            }
        }

    }

    private void sendMessage(final String email, final String message, StorageReference storageReference , Uri file, String s) {
        if (storageReference != null){
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name+"_gallery");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG,"onFailure sendFileFirebase "+e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG,"onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    FileModel fileModel = new FileModel("img",downloadUrl.toString(),name,"");
                    ChatMessage newChatMessage = new ChatMessage(System.currentTimeMillis(), email, message, 0, fileModel);
                    FirebaseUtility.getFireBaseChatRoomDatabaseReference().child(FirebaseUtility.generateUniqueEmailID(selectedUserID)).push().setValue(newChatMessage, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d(TAG, "Data could not be saved. " + databaseError.getMessage());
                            } else {
                                chatMessageAdapter.updateMessageStatus(databaseReference, 1);
                                Log.d(TAG, "File Data saved successfully.");

                                FirebaseUtility.queryChatMessagesFresh(selectedUserID);
                            }
                        }
                    });
                }
            });
        }else{
            //IS NULL
        }
    }

    private void sendsanpMessage(final String sender, final String message, final String fileUrl , StorageReference storageReference) {
        if (storageReference != null) {
            //UploadTask uploadTask = storageReference.putFile(Uri.fromFile(file));
            //uploadTask.addOnFailureListener(new OnFailureListener() {
            //@Override
            //public void onFailure(@NonNull Exception e) {
            //  Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
            //}
            //}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            //        @Override
            //  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            Log.i(TAG, "onSuccess sendFileFirebase");
            Uri downloadUrl = Uri.parse(fileUrl);
            FileModel fileModel = new FileModel("img", fileUrl, storageReference.child(fileUrl).getName(), storageReference.child(fileUrl).getMetadata() + "");
            ChatMessage newChatMessage = new ChatMessage(System.currentTimeMillis(), sender, message, 0, fileModel);
            FirebaseUtility.getFireBaseChatRoomDatabaseReference().child(FirebaseUtility.generateUniqueEmailID(selectedUserID)).push().setValue(newChatMessage, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d(TAG, "Data could not be saved. " + databaseError.getMessage());
                    } else {
                        chatMessageAdapter.updateMessageStatus(databaseReference, 1);
                        Log.d(TAG, "Data saved successfully.");
                        FirebaseUtility.queryChatMessages(selectedUserID);
                    }
                }
            });
        }
    }



//    private CharSequence converteTimestamp(String timestamp){
//        return DateUtils.getRelativeTimeSpanString(Long.parseLong(timestamp),System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
//    }

}