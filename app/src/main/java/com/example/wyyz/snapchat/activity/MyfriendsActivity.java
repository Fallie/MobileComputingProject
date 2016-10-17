package com.example.wyyz.snapchat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.wyyz.snapchat.Interface.CustomOnItemClickListener;
import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.User;
import com.example.wyyz.snapchat.util.OnSwipeTouchListener;
import com.example.wyyz.snapchat.util.AppConstants;
import com.example.wyyz.snapchat.util.TmpPhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Activity to display all my friends
 * It supports to pass image to a selected friend
 */
public class MyfriendsActivity extends AppCompatActivity implements CustomOnItemClickListener {
    public static final String TAG = "MyfriendsActivity";
    private ListView userListView;
    private ChatUserAdapter adapter;
    List<User> users=new ArrayList<User>();
    boolean containsImage=false;
    Bitmap base;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfriends);
        Intent intent=getIntent();
        if(intent.hasExtra("hasImage")){//contains image to pass
            containsImage=intent.getBooleanExtra("hasImage",false);
            Log.d("containsImage",String.valueOf(containsImage));
        }
        userListView = (ListView)findViewById(R.id.listView_id);
        userListView.setOnTouchListener(new OnSwipeTouchListener(this.getBaseContext(),MyfriendsActivity.this));
        getFriendsFromRemote();
        adapter = new  ChatUserAdapter(MyfriendsActivity.this, R.layout.single_friend_item, users,this);
        userListView.setAdapter(adapter);
    }

    //Get my friends from firebase
    private void getFriendsFromRemote(){
        //current user
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        //get friends uid
        DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(currentUser.getUid()).child("friends");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    Log.d("friendDataSnap",dataSnapshot.getValue().toString());
                    Map<String,Object>objectMap=(HashMap<String, Object>) dataSnapshot.getValue();
                    Set<String> set=new HashSet<String>();
                    Iterator<Map.Entry<String, Object>> entries = objectMap.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry<String, Object> entry = entries.next();
                        set.add(entry.getKey());
                    }

                    //get user instance of friend
                    for(String uid:set){
                        Log.d("Set",uid);
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                .child("Users").child(uid);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user=dataSnapshot.getValue(User.class);
                                users.add(user);
                                Log.d("Friend",user.toString());
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(View v, int position, Object value) {
        if(containsImage){
            progressDialog = ProgressDialog.show(MyfriendsActivity.this, "Send to Friend", "Sending...");
            sendSnaptoFriend((User)value);
        }else {
            Log.d("Click", ((User) value).getEmail());
            Intent i = new Intent(MyfriendsActivity.this, ChatActivity.class);
            i.putExtra(AppConstants.INTENT_GROUP_SELECTED_GROUP, ((User) value).getEmail());
            startActivity(i);
        }
    }
    //Send to friend, upload image to firebase
    private void sendSnaptoFriend(final User friend){
        base= TmpPhotoView.photo;
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        StorageReference photoRef = FirebaseStorage.getInstance().getReference("images")
                .child(timestamp.toString());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        base.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = photoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.i(TAG,"upload failed!!!");
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                String url=taskSnapshot.getDownloadUrl().toString();
                Log.i(TAG,"upload successful!!!");
                progressDialog.dismiss();
                Intent intentNext = new Intent(MyfriendsActivity.this, ChatActivity.class);
                intentNext.putExtra(AppConstants.INTENT_GROUP_SELECTED_GROUP, friend.getEmail());
                intentNext.putExtra("file_url",url);
                startActivity(intentNext);
                MyfriendsActivity.this.finish();
            }
        });
    }
}



