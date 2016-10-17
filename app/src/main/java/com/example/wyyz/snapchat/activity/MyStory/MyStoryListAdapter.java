//package dynamicard;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.dynamicard.ui.R;
//
//import java.util.List;
//
//import dynamicard.DBHelper.DiscoveryChannel;
//
///**
// * Created by leify on 2016/10/16.
// */
//
//public class SnapListAdapter extends BaseAdapter {
//    Context context;
//    List<String> list;
//    public SnapListAdapter(Context _context, List<String> _list) {
//        this.list = _list;
//        this.context = _context;
//    }
//
//    @Override
//    public int getCount() {
//        return list.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return list.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        LayoutInflater layoutInflater = LayoutInflater.from(context);
//
//        String snap = list.get(position);
//            convertView = layoutInflater.inflate(R.layout.mystory_nonheader, null);
//            ImageView imageView = (ImageView) convertView.findViewById(R.id.headerPreview);
//
//            TextView storyNameText = (TextView) convertView.findViewById(R.id.storyNameText);
//            TextView storyTimeText = (TextView) convertView.findViewById(R.id.storyTimeText);
//
////        CityItem city = list.get(position);
//
////        imageView.setImageBitmap(channel.getProfile());
//            storyNameText.setText(snap);
//            storyTimeText.setText(story.getTimeStamp());
//
//        return convertView;
//    }
//
//
//}





package com.example.wyyz.snapchat.activity.MyStory;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.FriendStory;
import com.example.wyyz.snapchat.model.MyStorySnap;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by leify on 2016/10/16.
 */

public class MyStoryListAdapter extends BaseExpandableListAdapter {

    Context context;
    List<FriendStory> myStory;
    private static final int MAX_ITEMS_MEASURED = 15;

    public MyStoryListAdapter(Context _context, List<FriendStory> _list) {
        this.myStory = _list;
        this.context = _context;
    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return myStory.get(groupPosition).getSnaps().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Log.e("create chile view",""+childPosition);
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        FriendStory story = myStory.get(groupPosition);
        MyStorySnap mySnap = story.getSnaps().get(childPosition);
        String url = mySnap.getPath();

            convertView = layoutInflater.inflate(R.layout.mystory_nonheader, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.headerPreview);
            Glide.with(context).load(Uri.parse(url)).into(imageView);

            TextView storyNameText = (TextView) convertView.findViewById(R.id.storyNameText);
            TextView storyTimeText = (TextView) convertView.findViewById(R.id.storyTimeText);

            storyNameText.setText("");
            storyTimeText.setText( new SimpleDateFormat("EEE, d MMM yyyy, HH:mm").format(mySnap.getTimestamp()));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return myStory.get(groupPosition).getSnaps().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return myStory.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return myStory.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, final boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            Log.e("create group view", "" + groupPosition);
            LayoutInflater layoutInflater = LayoutInflater.from(context);

            FriendStory story = myStory.get(groupPosition);
            if (story.getSnaps().size() > 0) {
                try {
                    convertView = layoutInflater.inflate(R.layout.mystory_header, null);
                    ImageView imageView = (ImageView) convertView.findViewById(R.id.headerPreview);

//            TextView storyNameText = (TextView) convertView.findViewById(R.id.storyNameText);
                    TextView storyTimeText = (TextView) convertView.findViewById(R.id.myStoryTimeText);
                    storyTimeText.setText(story.getTimeStamp());
                }catch(Exception e)
                {
                    e.printStackTrace();
                    Log.e("error",e.getMessage());
                }

//        CityItem city = list.get(position);

//        imageView.setImageBitmap(channel.getProfile());
//            storyNameText.setText(story.getName());
//
//                storyTimeText.setTextColor(context.getResources().getColor(R.color.host_background));



			ImageButton saveMemory = (ImageButton) convertView.findViewById(R.id.SaveMemory);
                saveMemory.setFocusable(false);
			saveMemory.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					String email = searchView.getText().toString();
					Log.e("start activity", "save to memory");
				}
			});
			ImageButton addSnap = (ImageButton) convertView.findViewById(R.id.AddSnap);
                addSnap.setFocusable(false);
			addSnap.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					String email = searchView.getText().toString();
					Log.e("open activity", "camera");
				}
			});
            } else {
                convertView = layoutInflater.inflate(R.layout.mystory_nonheader, null);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.headerPreview);

                TextView storyNameText = (TextView) convertView.findViewById(R.id.storyNameText);
                TextView storyTimeText = (TextView) convertView.findViewById(R.id.storyTimeText);

//        CityItem city = list.get(position);

//        imageView.setImageBitmap(channel.getProfile());
                storyNameText.setText(story.getName());
                storyTimeText.setText(story.getTimeStamp());
                storyTimeText.setText("Tap to add a Snap!");
                storyTimeText.setTextColor(context.getResources().getColor(R.color.dark_gray));

                ImageView imageView1 = (ImageView) convertView.findViewById(R.id.headerPreview);

                convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					String email = searchView.getText().toString();
					Log.e("open activity", "camera");
				}
			});
            }
        }
        return convertView;
    }

    private int measureChildrenHeight(int groupPosition)
    {
        int height = 0;
        View itemView = null;
        LinearLayout viewGroup = new LinearLayout(context);
        viewGroup.setOrientation(LinearLayout.VERTICAL);

        final int widthMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        // Make sure the number of items we'll measure is capped. If it's a huge data set
        // with wildly varying sizes, oh well.
        int start = 0;//Math.max(0, getSelectedItemPosition());

        final int end = Math.min(getChildrenCount(groupPosition), start + MAX_ITEMS_MEASURED);
        final int count = end - start;
        start = Math.max(0, start - (MAX_ITEMS_MEASURED - count));
        for (int i = start; i < end; i++)
        {
            itemView = getChildView(groupPosition, i, (i+1 == end), itemView, viewGroup);
            if (itemView.getLayoutParams() == null)
            {
                itemView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            if(i+1 != end)
                height += itemView.getMeasuredHeight();//Math.max(height, itemView.getMeasuredHeight());
        }

        return height;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
