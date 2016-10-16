package com.example.wyyz.snapchat.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.wyyz.snapchat.Interface.CustomOnItemClickListener;
import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.User;

import java.util.List;

/**
 * Created by Scott Yang on 14/10/2016.
 */

public class ChatUserAdapter extends ArrayAdapter<User> {
    private int resourceId;
    private CustomOnItemClickListener listener;
    public ChatUserAdapter(Context context, int textViewResourceId, List<User> objects, CustomOnItemClickListener listener){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
        this.listener = listener;
    }
    public View getView(final int position, View convertView, ViewGroup parent){
        final User user=getItem(position);
        final View view;
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(view, position, user);
            }
        });
//        view.setOnClickListener(new View.OnClickListener(){
//            public  void  onClick(View v){
//                listener.clickImageChat(view,position,user.getUsername(),);
//            }
//        });
        viewHolder.username.setText(user.getUsername());
        viewHolder.email.setText(user.getEmail());
        return view;
    }
}

class CharViewHolder{
    TextView username;
    TextView email;
    TextView accept;
    TextView ignore;
}
