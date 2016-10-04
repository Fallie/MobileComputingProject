package com.example.wyyz.snapchat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.User;
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

public class MyfriendsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ListView userListView;
    private SearchView searchView;
    private UserAdapter adapter;
    List<User> users=new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfriends);
        searchView = (SearchView)findViewById(R.id.searchView_id);
        userListView = (ListView)findViewById(R.id.listView_id);
        getFriendsFromRemote();
        adapter = new UserAdapter(MyfriendsActivity.this, R.layout.single_friend_item, users);
        userListView.setAdapter(adapter);
        userListView.setTextFilterEnabled(true);

        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void getFriendsFromRemote(){
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
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
                    for(String uid:set){
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                .child("Users").child(uid);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user=dataSnapshot.getValue(User.class);
                                users.add(user);
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
    public boolean onQueryTextSubmit(String query) {
        // TODO
       return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO
        if(TextUtils.isEmpty(newText)) {
            //clear the filter of listview
            userListView.clearTextFilter();
        }
        else {
            //set filter to listview based on query
            adapter.getFilter().filter(newText);
        }
        return true;
    }
}



