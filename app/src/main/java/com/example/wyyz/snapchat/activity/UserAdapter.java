package com.example.wyyz.snapchat.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.User;

import java.util.List;

/**
 * Created by ZIYUAN on 11/10/2016.
 */

/**
 * User Adapter is used in AddFriendByUsername Activity and MyfriendsActivity
 */
class UserAdapter extends ArrayAdapter<User> {
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
    TextView accept;
    TextView ignore;
}
