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
import android.text.format.DateUtils;
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
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.FileModel;
import com.example.wyyz.snapchat.util.AppConstants;

import com.example.wyyz.snapchat.util.FirebaseUtility;
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


public class ChatActivity extends BaseActivity implements CustomOnItemClickListener, View.OnClickListener ,CustomClickImageListener{

    private static final String TAG = ChatActivity.class.getSimpleName();
    private RecyclerView recyclerViewChat;
    private TextView textViewNoConversations;
    private Button buttonSendMessage,buttonSendPic;
    private EditText editTextSendMessage;
    private ChatMessageAdapter chatMessageAdapter;
    private Toolbar toolbar;
    private String selectedUserID;
    private String fileUrl;
    private File filePathImageCamera;
    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getIntentExtras();
        if (fileUrl!=null){

        }else{
        initComponents();
        addListeners();
        setUpToolbar();
        }
    }

    private void getIntentExtras() {
        selectedUserID = getIntent().getStringExtra(AppConstants.INTENT_GROUP_SELECTED_GROUP);
        Log.v(TAG,selectedUserID);
        if(getIntent().getStringArrayExtra(FILE_URL)!= null){
            fileUrl = getIntent().getStringExtra(FILE_URL);
            Log.v(TAG, fileUrl);
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
                    }
                }
            });
        }
    }



    @Override
    public void onItemClick(View v, int position, Object value) {
        Log.e(TAG, "Item Clicked at position - " + position + " and value - " + value);
    }


    public void clickImageChat(View view, int position,String nameUser,String urlPhotoClick) {
       // int position = getAdapterPosition();
        //ChatModel model = getItem(position);

        CustomClickImageListener customClickImageListener = null;

        CustomOnItemClickListener customOnItemClickListener = null;


        //customOnItemClickListener.clickImageChat(view,position,nameUser,urlPhotoClick);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSendMessage:
                if(TextUtils.isEmpty(editTextSendMessage.getText().toString().trim())){
                    showInputFieldError(editTextSendMessage, getString(R.string.enter_chat_message));
                }
                else{
                    sendMessage(FirebaseUtility.getCurrentUser().getEmail(), editTextSendMessage.getText().toString(),null,null);
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

    @Override
    public void onItemClick(View view, int position, String nameUser, String urlPhotoClick) {
        CustomClickImageListener customClickImageListener = null;
        customClickImageListener.onItemClick(view,position,nameUser,urlPhotoClick);
    }

    private static class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessagesViewHolder> {

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

            String uname = SnapChatDB.getInstance(context).findUserByEmail(chatMessageList.get(position).getSender())
                    .getUsername();
            if (uname!=null){
                messagesViewHolder.textViewSender.setText(uname);
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
            }

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

        public static class MessagesViewHolder extends RecyclerView.ViewHolder {

            public TextView textViewSender, textViewMessage, textViewTime;
            private ImageView imageView;

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

              //  imageView.setOnClickListener((View.OnClickListener) this);
            }
        }
    }
    /**
     * Enviar foto tirada pela camera
     */
    private void photoCameraIntent(){
        String nomeFoto = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto+"camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(filePathImageCamera));
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }

    /**
     * Enviar foto pela galeria
     */
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
        }else if (requestCode == IMAGE_CAMERA_REQUEST){
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
                                Log.d(TAG, "Data saved successfully.");

                                FirebaseUtility.queryChatMessages(email);
                            }
                        }
                    });
                }
            });
        }else{
            //IS NULL
        }
    }


    private CharSequence converteTimestamp(String timestamp){
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(timestamp),System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

}
