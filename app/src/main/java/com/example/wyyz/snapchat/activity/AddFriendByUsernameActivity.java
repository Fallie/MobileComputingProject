package com.example.wyyz.snapchat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddFriendByUsernameActivity extends AppCompatActivity implements View.OnClickListener{
    private ListView userListView;
    private EditText inputText;
    private Button search;
    private TextView noUser;
    private UserAdapter adapter;
    List<User> users=new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_by_username);
        inputText=(EditText)findViewById(R.id.input_text);
        userListView=(ListView)findViewById(R.id.user_list_view);
        noUser=(TextView)findViewById(R.id.tv_no_user);
        search=(Button)findViewById(R.id.btn_search);
        search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                if(!users.isEmpty()){
                    users.clear();
                }
                String name=inputText.getText().toString();
                searchUsersByUsername(name);
                break;
            default:
                break;
        }
    }
    private void searchUsersByUsername(String username){
        DatabaseReference usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        Query queryRef=usersRef.orderByChild("username").equalTo(username);
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    Log.d("dataSnapshot", dataSnapshot.toString());
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    Log.d("objectMap is a user?", objectMap.toString());
                    User user = null;
                    String uid = null;
                    Iterator<Map.Entry<String, Object>> entries = objectMap.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry<String, Object> entry = entries.next();
                        // System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                        uid = entry.getKey();
                        Log.d("frienduid", uid);
                        Map<String, Object> userMap = (HashMap<String, Object>) entry.getValue();
                        Gson gson = new Gson();
                        String userJson = gson.toJson(userMap);
                        user = gson.fromJson(userJson, User.class);
                        Log.d("friendjson", userJson);
                        users.add(user);
                    }

                    if (!users.isEmpty()) {
                        noUser.setVisibility(View.GONE);
                        userListView.setVisibility(View.VISIBLE);
                        adapter = new UserAdapter(AddFriendByUsernameActivity.this, R.layout.user_item, users);
                        userListView.setAdapter(adapter);
                        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                sendFriendRequest(users.get(position));
                            }
                        });
                    }
                }else{
                    userListView.setVisibility(View.VISIBLE);
                    noUser.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void sendFriendRequest(User user){
        FriendRequestHelper.sendRequest(user);
        Toast.makeText(AddFriendByUsernameActivity.this, "Request successfully sent, please wait for response.",
                Toast.LENGTH_LONG).show();
        users.remove(user);
        adapter.notifyDataSetChanged();
    }
}

class FriendRequestHelper{
    public static void sendRequest(User user){
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        Date time=new Date();
        DateFormat df = new SimpleDateFormat("MMddyyyyHH:mm:ss");
        String timeString=df.format(time);
        String id=currentUser.getUid()+timeString;
        Log.d("Mystring", id);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("FriendRequests").child(id);

        db.child("fromUid").setValue(currentUser.getUid());
        db.child("fromEmail").setValue(currentUser.getEmail());
        //db.child("toUid").setValue(uid.toString());
        db.child("toEmail").setValue(user.getEmail());
        db.child("timestamp").setValue(ServerValue.TIMESTAMP);
    }
}
