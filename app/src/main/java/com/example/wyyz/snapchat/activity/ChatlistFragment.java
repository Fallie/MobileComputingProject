package com.example.wyyz.snapchat.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.wyyz.snapchat.model.Friend;
import com.example.wyyz.snapchat.model.User;
import com.example.wyyz.snapchat.R;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatlistFragment extends Fragment {

    ListView listView;
    FriendAdapter adapter;
    List<Friend> friends=new ArrayList<>();

    public ChatlistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View chatlistView=inflater.inflate(R.layout.fragment_chatlist, container, false);
        SnapActivity snapActivity = (SnapActivity) getActivity();
        User user=snapActivity.getDbInstance().findUserByUsername("ziyuan_w");
        friends= snapActivity.getDbInstance().getFriends(user.getId());
        listView=(ListView) chatlistView.findViewById(R.id.list_view);
        adapter=new FriendAdapter(snapActivity,R.layout.single_friend_item,friends);
        listView.setAdapter(adapter);
        return chatlistView;
    }

}
