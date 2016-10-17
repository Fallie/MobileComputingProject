package com.example.wyyz.snapchat.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.Snap;
import com.example.wyyz.snapchat.model.Story;

import java.util.ArrayList;

/**
 * Created by ZIYUAN on 17/10/2016.
 */

public class StoryAdapter extends ArrayAdapter{
    private Context context;
    private int layoutResourceId;
    private SnapChatDB db;

    public StoryAdapter(Context context, int layoutResourceId, ArrayList data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        db=SnapChatDB.getInstance(context);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ImageAdapter.ViewHolder holder = null;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(layoutResourceId, parent, false);
            holder = new ImageAdapter.ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.iv_image);
            view.setTag(holder);
        } else {
            holder = (ImageAdapter.ViewHolder) view.getTag();
        }
        Story story=(Story)getItem(position);
        Snap snap=db.getStoryFirstSnap(story);
        //holder.image.setImageBitmap(item.getPhoto());
        Glide.with(context)
                .load(snap.getPath())
                .centerCrop()
                .crossFade()
                .into(holder.image);


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(context, "Single Photo Edit!",
                        Toast.LENGTH_SHORT).show();*/
                /*Intent intent = new Intent(context,OpenMySnapActivity.class);
                intent.putExtra("SnapPath",item.getPath());
                intent.putExtra("TimeStamp",item.getTimestamp());
                intent.putExtra("Size",item.getSize());
                context.startActivity(intent);*/

            }
        });
        return view;
    }

}
