package com.example.wyyz.snapchat.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Activity to display all the friend requests who added me.
 * Can accept or ignore the friend request here
 */
public class AddedmeActivity extends AppCompatActivity {
    private ListView requestListView;
    private TextView noRequest;
    private RequestAdapter adapter;
    List<User> users = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addedme);
        requestListView = (ListView) findViewById(R.id.request_list_view);
        noRequest = (TextView) findViewById(R.id.tv_noNewFriend);
        getRequests();
    }

    //get friendRequests from remote
    private void getRequests() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String myEmail = currentUser.getEmail();
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        final Query queryRef = requestRef.orderByChild("toEmail").equalTo(myEmail);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    Map<String, Object> objMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    Iterator<Map.Entry<String, Object>> entries = objMap.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry<String, Object> entry = entries.next();
                        Map<String, Object> objectMap=(HashMap<String, Object>)entry.getValue();
                        final String fromUid = (String) objectMap.get("fromUid");
                        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUid);
                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                Log.d("Im here","1");
                                boolean exist=false;
                                for(User u:users){
                                    if(u.getEmail()==user.getEmail()){
                                        exist=true;
                                    }
                                }
                                if(!exist) {
                                    users.add(user);
                                }
                                if (!users.isEmpty()) {
                                    noRequest.setVisibility(View.GONE);
                                    requestListView.setVisibility(View.VISIBLE);
                                    adapter = new RequestAdapter(AddedmeActivity.this, R.layout.request_item, users);
                                    requestListView.setAdapter(adapter);
                                }
                                userRef.removeEventListener(this);
                                queryRef.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }else{
                    Log.d("Im here","2");
                    noRequest.setVisibility(View.VISIBLE);
                    requestListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

class RequestAdapter extends ArrayAdapter<User> {
    private int resourceId;
    private List<User> users;
    private RequestAdapter adapter;

    public RequestAdapter(Context context, int textViewResourceId, List<User> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        users=objects;
        adapter=this;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.username = (TextView) view.findViewById(R.id.tv_username);
            viewHolder.email = (TextView) view.findViewById(R.id.tv_email);
            viewHolder.accept=(TextView) view.findViewById(R.id.tv_accept);
            viewHolder.ignore=(TextView)view.findViewById(R.id.tv_ignore);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.username.setText(user.getUsername());
        viewHolder.email.setText(user.getEmail());
        viewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRequest(user);
            }
        });
        viewHolder.ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             ignoreRequest(user);
            }
        });
        return view;
    }
    //ignore the Request
    private void ignoreRequest(User user){
        //delete from firebase
        final FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String fromEmail=user.getEmail();
        DatabaseReference usersRef= FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        Query queryRef=usersRef.orderByChild("fromEmail").equalTo(fromEmail);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("dataSnapshot",dataSnapshot.toString());
                Log.d("ref",dataSnapshot.getRef().toString());
                if(dataSnapshot.getValue()!=null) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    Iterator<Map.Entry<String, Object>> entries = objectMap.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry<String, Object> entry = entries.next();
                        String uid = entry.getKey();
                        Map<String, Object> requestMap=(HashMap<String, Object>)entry.getValue();
                        Log.d("reqestMap",requestMap.toString());
                        String toEmail= requestMap.get("toEmail").toString();
                        if(toEmail.equals(currentUser.getEmail())) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("FriendRequests").child(uid);
                            Log.d("ref", ref.toString());
                            ref.removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //delete from listview
        users.remove(user);
        adapter.notifyDataSetChanged();
    }

    //accept the friend request
    private void acceptRequest(User user){
        String fromEmail=user.getEmail();
        //delete from listview
        users.remove(user);
        adapter.notifyDataSetChanged();
        //add friendship
        DatabaseReference usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        Query queryRef=usersRef.orderByChild("email").equalTo(fromEmail);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("dataSnapshot",dataSnapshot.toString());
                Log.d("ref",dataSnapshot.getRef().toString());
                if(dataSnapshot.getValue()!=null) {
                    String fromUid = null;
                    final FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
                    String toUid=currentUser.getUid();
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    Iterator<Map.Entry<String, Object>> entries = objectMap.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry<String, Object> entry = entries.next();
                        fromUid = entry.getKey();

                        DatabaseReference user1Ref= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUid);
                        user1Ref.child("friends").child(toUid).setValue(ServerValue.TIMESTAMP);
                        DatabaseReference user2Ref= FirebaseDatabase.getInstance().getReference().child("Users").child(toUid);
                        user2Ref.child("friends").child(fromUid).setValue(ServerValue.TIMESTAMP);
                    }
                    //delete request in firebase
                    DatabaseReference requestRef= FirebaseDatabase.getInstance().getReference().child("FriendRequests");
                    Query queryRef=requestRef.orderByChild("fromUid").equalTo(fromUid);
                    queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("dataSnapshot",dataSnapshot.toString());
                            Log.d("ref",dataSnapshot.getRef().toString());
                            if(dataSnapshot.getValue()!=null) {
                                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                Iterator<Map.Entry<String, Object>> entries = objectMap.entrySet().iterator();
                                while (entries.hasNext()) {
                                    Map.Entry<String, Object> entry = entries.next();
                                    String uid = entry.getKey();
                                    Map<String, Object> requestMap=(HashMap<String, Object>)entry.getValue();
                                    Log.d("reqestMap",requestMap.toString());
                                    String toEmail= requestMap.get("toEmail").toString();
                                    if(toEmail.equals(currentUser.getEmail())) {
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("FriendRequests").child(uid);
                                        Log.d("ref", ref.toString());
                                        ref.removeValue();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}



