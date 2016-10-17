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
//This is to adapt to the recently coming stories
public class newStoryListAdapter extends BaseAdapter {
    Context context;
    ArrayList<ArrayList<FriendStorySnap>> list;
    public newStoryListAdapter(Context _context, ArrayList<ArrayList<FriendStorySnap>> _list) {
        this.list = parse(_list);
        this.context = _context;
    }

    private ArrayList<ArrayList<FriendStorySnap>> parse(ArrayList<ArrayList<FriendStorySnap>> _list)
    {
        ArrayList<ArrayList<FriendStorySnap>> list = _list;
//                new ArrayList<ArrayList<FriendStorySnap>>();
//        for(int i =0;i<_list.size();i++)
//        {
//            ArrayList<FriendStorySnap> story = new ArrayList<FriendStorySnap>();
//            for(int r=0;r<_list.get(i).size();r++)
////                if(isIndate(_list.get(i).get(r))&&isRecent(_list.get(i).get(r)))
//                    story.add(_list.get(i).get(r));
////            if(story.size()>0)
//                list.add(story);
//        }
        Log.e("copy length",""+list.size());
        return list;
    }

    private boolean isRecent(FriendStorySnap snap)
    {
//        if(snap.getVisitNum()<2)
//            return true;
//        else
//            return false;
        return true;
    }

    private boolean isIndate(FriendStorySnap snap)
    {
//        Date date = Calendar.getInstance().getTime();
//        Date snapDate = snap.getTimestamp();
//
//        long day = date.getTime() / (24*60*60*1000) - snapDate.getTime() / (24*60*60*1000);
//
//        if(!(day>1))
//            return true;
//        else
//            return false;
        return true;
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


        //the snap is not read yet
        if(list.get(position).size()>0) {
            List<FriendStorySnap> story = list.get(position);
            if (getLowestNum(list.get(position)) == 0) {
                convertView = layoutInflater.inflate(R.layout.mystory_nonheader, null);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.headerPreview);
                Glide.with(context).load(Uri.parse(list.get(position).get(0).getPath())).into(imageView);


                TextView storyNameText = (TextView) convertView.findViewById(R.id.storyNameText);
                TextView storyTimeText = (TextView) convertView.findViewById(R.id.storyTimeText);

//        CityItem city = list.get(position);

//        imageView.setImageBitmap(channel.getProfile());
                storyNameText.setText(list.get(position).get(0).getUserName());
                storyTimeText.setText(list.get(position).get(0).getTimestamp().toString());
            } else if (getLowestNum(list.get(position)) == 1) {
                convertView = layoutInflater.inflate(R.layout.mystory_nonheader, null);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.headerPreview);
                Glide.with(context).load(Uri.parse(list.get(position).get(0).getPath())).into(imageView);

                TextView storyNameText = (TextView) convertView.findViewById(R.id.storyNameText);
                TextView storyTimeText = (TextView) convertView.findViewById(R.id.storyTimeText);

//        CityItem city = list.get(position);

//        imageView.setImageBitmap(channel.getProfile());
                storyNameText.setText(list.get(position).get(0).getUserName());
                storyTimeText.setText("tap to replay");
            }
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
