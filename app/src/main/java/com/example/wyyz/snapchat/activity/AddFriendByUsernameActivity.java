package com.example.wyyz.snapchat.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.ServerValue;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFriendByUsernameActivity extends AppCompatActivity implements View.OnClickListener{
    private ListView userListView;
    private EditText inputText;
    private Button search;
    private TextView noUser;
    private UserAdapter adapter;
    private boolean noFriend=true;
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
                noFriend=true;
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
        queryRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                noFriend=false;
               Map<String, Object> objectMap=(HashMap<String, Object>) dataSnapshot.getValue();
                Log.d("objectMap is a user?", objectMap.toString());
                Gson gson=new Gson();
                String userJson=gson.toJson(objectMap);
                User user=gson.fromJson(userJson,User.class);
                users.add(user);
                if(!users.isEmpty()){
                    noUser.setVisibility(View.GONE);
                    userListView.setVisibility(View.VISIBLE);
                    adapter=new UserAdapter(AddFriendByUsernameActivity.this,R.layout.user_item,users);
                    userListView.setAdapter(adapter);
                    userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            sendFriendRequest(users.get(position));
                        }
                    });
                }
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
        if(noFriend){
            userListView.setVisibility(View.VISIBLE);
            noUser.setVisibility(View.VISIBLE);
        }
    }
    private void sendFriendRequest(User user){
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String email=currentUser.getEmail();
        Date time=new Date();
        DateFormat df = new SimpleDateFormat("MMddyyyyHH:mm:ss");
        String timeString=df.format(time);
        String uid=currentUser.getUid()+timeString;
        Log.d("Mystring", uid);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("FriendRequests").child(uid);
        //DatabaseReference thisRef = database.getReference("FriendRequests");
        //DatabaseReference db=thisRef.child(uid);
        Log.d("fromEmail",email);
        Log.d("toEmail",user.getEmail());
        db.child("fromemail").setValue(email.toString());
        db.child("toemail").setValue(user.getEmail().toString());
        db.child("timestamp").setValue(ServerValue.TIMESTAMP);
        users.remove(user);
        adapter.notifyDataSetChanged();
        Toast.makeText(AddFriendByUsernameActivity.this, "Request successfully sent, please wait for response.",
                Toast.LENGTH_LONG).show();
    }
}

class UserAdapter extends ArrayAdapter<User>{
    private int resourceId;
    public UserAdapter(Context context, int textViewResourceId, List<User> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        User user=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder=new ViewHolder();
            viewHolder.username=(TextView)view.findViewById(R.id.tv_username);
            viewHolder.email=(TextView)view.findViewById(R.id.tv_email);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.username.setText(user.getUsername());
        viewHolder.email.setText(user.getEmail());
        return view;
    }
}
class ViewHolder{
    TextView username;
    TextView email;
}
