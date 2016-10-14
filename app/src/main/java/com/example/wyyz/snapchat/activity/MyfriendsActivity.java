package com.example.wyyz.snapchat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.User;
import com.example.wyyz.snapchat.util.OnSwipeTouchListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyfriendsActivity extends AppCompatActivity {
    public static final String TAG = "MyfriendsActivity";
    private ListView userListView;
    private UserAdapter adapter;
    List<User> users=new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfriends);
        userListView = (ListView)findViewById(R.id.listView_id);
        userListView.setOnTouchListener(new OnSwipeTouchListener(this.getBaseContext(),MyfriendsActivity.this));
        getFriendsFromRemote();
        adapter = new UserAdapter(MyfriendsActivity.this, R.layout.single_friend_item, users);
        userListView.setAdapter(adapter);
    }

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

}



