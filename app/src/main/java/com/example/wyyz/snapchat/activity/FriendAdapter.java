package com.example.wyyz.snapchat.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.Friend;
import com.example.wyyz.snapchat.model.Friend;

import java.util.List;

/**
 * Created by ZIYUAN on 6/09/2016.
 */
public class FriendAdapter extends ArrayAdapter<Friend>{
    private int resourceId;

    public FriendAdapter(Context context, int resource, List<Friend> objects){
        super(context,resource,objects);
        this.resourceId=resource;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        Friend friend=getItem(position);
        ViewHolder viewHolder;
        View view;
        if(convertView==null){
            viewHolder=new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder.tvFriendName=(TextView) view.findViewById(R.id.tv_friendname);
            viewHolder.tvChatTime=(TextView) view.findViewById(R.id.tv_chattime);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }

        if(friend.getEditedName()!=null){
            viewHolder.tvFriendName.setText(friend.getEditedName());
        }else {
            viewHolder.tvFriendName.setText(friend.getNickName());
        }
        viewHolder.tvChatTime.setText(friend.getLastChatTimeStamp().toString());
        return view;
    }

    class ViewHolder{
        TextView tvFriendName;
        TextView tvChatTime;
    }
}
