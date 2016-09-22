package com.example.wyyz.snapchat.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.ChatRecord;
import com.example.wyyz.snapchat.model.Friend;

import java.util.List;


/**
 * Created by ZYUAN on 22/09/2016.
 */

public class MsgAdapter extends ArrayAdapter<ChatRecord>{
    private int resourceId;
    private Friend friend;
    public MsgAdapter(Context context, int resource, List<ChatRecord> objects, Friend friend1) {
        super(context, resource, objects);
        resourceId=resource;
        friend=friend1;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        ChatRecord msg=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder=new ViewHolder();
            viewHolder.meLayout=(LinearLayout)view.findViewById(R.id.me_layout);
            viewHolder.senderLayout=(LinearLayout)view.findViewById(R.id.sender_layout);
            viewHolder.meMsg=(TextView)view.findViewById(R.id.me_msg);
            viewHolder.senderMsg=(TextView)view.findViewById(R.id.sender_msg);
            viewHolder.sender=(TextView)view.findViewById(R.id.sender_name);
            view.setTag(viewHolder);
        }else {
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }
        if(msg.getType()==ChatRecord.TYPE_RECEIVED){
            viewHolder.senderLayout.setVisibility(View.VISIBLE);
            viewHolder.meLayout.setVisibility(View.GONE);
            viewHolder.sender.setText(friend.getNickName()+": ");
            viewHolder.senderMsg.setText(msg.getContent());//did not handle picture yet
        }else if (msg.getType()==ChatRecord.TYPE_SENT){
            viewHolder.meLayout.setVisibility(View.VISIBLE);
            viewHolder.senderLayout.setVisibility(View.GONE);
            viewHolder.meMsg.setText(msg.getContent());
        }
        return view;
    }
    class ViewHolder {
        LinearLayout senderLayout;
        LinearLayout meLayout;
        TextView senderMsg;
        TextView meMsg;
        TextView sender;
    }
}
