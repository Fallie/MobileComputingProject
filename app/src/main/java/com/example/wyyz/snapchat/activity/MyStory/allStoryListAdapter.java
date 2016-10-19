package com.example.wyyz.snapchat.activity.MyStory;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.FriendStory;
import com.example.wyyz.snapchat.model.FriendStorySnap;
import com.example.wyyz.snapchat.model.MyStorySnap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by leify on 2016/10/16.
 */
//This class is use to adapt the AllStoryList
public class allStoryListAdapter extends BaseAdapter {
    Context context;
    ArrayList<ArrayList<FriendStorySnap>> list;
    public allStoryListAdapter(Context _context, ArrayList<ArrayList<FriendStorySnap>> _list) {
        this.list = parse(_list);
        this.context = _context;
    }

    private ArrayList<ArrayList<FriendStorySnap>> parse(ArrayList<ArrayList<FriendStorySnap>> _list)
    {
        ArrayList<ArrayList<FriendStorySnap>> list = _list;

        Log.e("copy length",""+list.size());
        return list;
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

    //to get the sub view
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if(list.get(position).size()>0) {
            List<FriendStorySnap> story = list.get(position);

                convertView = layoutInflater.inflate(R.layout.mystory_nonheader, null);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.headerPreview);
                Glide.with(context).load(Uri.parse(list.get(position).get(0).getPath())).into(imageView);

                TextView storyNameText = (TextView) convertView.findViewById(R.id.storyNameText);
                TextView storyTimeText = (TextView) convertView.findViewById(R.id.storyTimeText);

//        CityItem city = list.get(position);
            Date time=list.get(position).get(0).getTimestamp();
            for(FriendStorySnap snap: list.get(position))
                if(time.before(snap.getTimestamp()))
                    time = snap.getTimestamp();
//        imageView.setImageBitmap(channel.getProfile());
                storyNameText.setText(list.get(position).get(0).getUserName());
                storyTimeText.setText(time.toString());

        }
        return convertView;
    }

    private int getLowestNum(List<FriendStorySnap> story)
    {
        int num=-1;
        for(int i =0;i<story.size();i++)
            if(story.get(i).getVisitNum()>num)
                num = story.get(i).getVisitNum();
        return num;
    }


}
