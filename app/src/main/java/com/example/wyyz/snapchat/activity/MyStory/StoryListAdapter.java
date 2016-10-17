package com.example.wyyz.snapchat.activity.MyStory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.FriendStory;

import java.util.List;

/**
 * Created by leify on 2016/10/16.
 */

public class StoryListAdapter extends BaseAdapter {
    Context context;
    List<FriendStory> list;
    public StoryListAdapter(Context _context, List<FriendStory> _list) {
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

        FriendStory story = list.get(position);
        if(story.getVisitNum()==0) {
            convertView = layoutInflater.inflate(R.layout.mystory_nonheader, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.headerPreview);

            TextView storyNameText = (TextView) convertView.findViewById(R.id.storyNameText);
            TextView storyTimeText = (TextView) convertView.findViewById(R.id.storyTimeText);

//        CityItem city = list.get(position);

//        imageView.setImageBitmap(channel.getProfile());
            storyNameText.setText(story.getName());
            storyTimeText.setText(story.getTimeStamp());
        }
        else if(story.getVisitNum()==1) {
            convertView = layoutInflater.inflate(R.layout.mystory_nonheader, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.headerPreview);

            TextView storyNameText = (TextView) convertView.findViewById(R.id.storyNameText);
            TextView storyTimeText = (TextView) convertView.findViewById(R.id.storyTimeText);

//        CityItem city = list.get(position);

//        imageView.setImageBitmap(channel.getProfile());
            storyNameText.setText(story.getName());
            storyTimeText.setText("tap to replay");
        }
        return convertView;
    }


}
