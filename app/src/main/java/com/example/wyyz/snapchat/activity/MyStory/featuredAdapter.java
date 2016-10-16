package com.example.wyyz.snapchat.activity.MyStory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.DiscoveryChannel;

import java.util.List;



/**
 * Created by leify on 2016/10/14.
 */

public class featuredAdapter extends BaseAdapter {
    Context context;
    List<DiscoveryChannel> list;
    public featuredAdapter(Context _context, List<DiscoveryChannel> _list) {
        this.list = _list;
        this.context = _context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.activity_cornerstone_item, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);

//        CityItem city = list.get(position);

        DiscoveryChannel channel = list.get(position);
        imageView.setImageBitmap(channel.getProfile());
//        Log.e("add grid view",""+channel.getChannelId());
        return convertView;
    }

    public List<DiscoveryChannel> getData()
    {
        return this.list;
    }
}
