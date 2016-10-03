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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddedmeActivity extends AppCompatActivity {
    private ListView requestListView;
    private TextView noRequest;
    private RequestAdapter adapter;
    List<User> users=new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addedme);
        requestListView=(ListView)findViewById(R.id.request_list_view);
        noRequest=(TextView)findViewById(R.id.tv_noNewFriend);
        getRequests();

    }
    private void getRequests(){
        Log.d("Im here","1");
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String myUid=currentUser.getUid();
        DatabaseReference requestRef= FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        Query queryRef=requestRef.orderByChild("toUid").equalTo(myUid);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Im here","2");
                Map<String, Object> objectMap=(HashMap<String, Object>) dataSnapshot.getValue();
                final String fromUid=(String)objectMap.get("fromUid");
                DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUid);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user=dataSnapshot.getValue(User.class);
                        users.add(user);
                        Log.d("user",user.toString());
                        Log.d("dataSnapshot",dataSnapshot.toString());
                        if(!users.isEmpty()){
                            noRequest.setVisibility(View.GONE);
                            requestListView.setVisibility(View.VISIBLE);
                            adapter=new RequestAdapter(AddedmeActivity.this,R.layout.request_item,users);
                            requestListView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Log.d("Im here","4");

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
                 Log.d("Does is work?","Accept yes!");
                String fromEmail=user.getEmail();
                DatabaseReference usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
                Query queryRef=usersRef.orderByChild("email").equalTo(fromEmail);
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
                                String fromUid = entry.getKey();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
        viewHolder.ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Does is work?","Ignore yes!");
                //delete from firebase
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
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("FriendRequests").child(uid);
                                Log.d("ref", ref.toString());
                                ref.removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                users.remove(user);
                adapter.notifyDataSetChanged();

            }
        });
        return view;
    }
}


