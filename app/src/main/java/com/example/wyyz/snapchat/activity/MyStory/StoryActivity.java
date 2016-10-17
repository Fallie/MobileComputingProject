package com.example.wyyz.snapchat.activity.MyStory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;


import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.activity.DiscoverActivity;
import com.example.wyyz.snapchat.activity.DiscoverChannelActivity;
import com.example.wyyz.snapchat.db.DataBaseOperator;
import com.example.wyyz.snapchat.model.DiscoveryChannel;
import com.example.wyyz.snapchat.model.FriendStory;
import com.example.wyyz.snapchat.util.OnSwipeTouchListener;

import java.util.ArrayList;
import java.util.List;



public class StoryActivity extends Activity {
	public static final String TAG = "StoryActivity";
	private static final int COLUMNCOUNT = 2;//列数
	private int columnWidth = 250;// 每个item的宽度
	private int itemHeight = 0;
	private int rowCountPerScreen = 2;
	private int cols = 2;// 当前总列数
	private ArrayList<Integer> colYs = new ArrayList<Integer>();
//	private ArrayList<View> currentViews = new ArrayList<View>();
	private LayoutInflater mInflater;
	private ArrayList<DiscoveryChannel> unsubscribed;
	private ArrayList<DiscoveryChannel> subscribed;

//	private FinalBitmap fb;
	private List<String> infos = new ArrayList<String>();
	private List<Point> lostPoint = new ArrayList<Point>();// 用于记录空白块的位置
//	private int currentPage = 1;

	private ScrollView frame;
	private ListView subscriptionList;
	private SearchView searchView;
	private ImageButton searchButton;
	private ImageButton discoverButton;
	private ExpandableListView storyList;
	private ListView friendStoryList;
	private GridView featureList;
	private TextView rootSubscriptionText;
	private TextView featuredText;
	private TextView friendStoryText;
	private Button searchFriendButton;
	private ImageButton ChatButton;
	private ImageButton CameraButton;
	private ImageButton StoryButton;



	private DataBaseOperator DBOperator;
	private ArrayList<DiscoveryChannel> channels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.avtivity_story);

		DBOperator = new DataBaseOperator(this);
		DBOperator.initialise();
//		DBOperator.update("leif@gmail.com");

		try{
			init();}
		catch(Exception e)
		{
			Log.e("error",e.getMessage());
			e.printStackTrace();}
		Log.e("state","finish init");

//		CornerstoneControl cornerstoneControl = new CornerstoneControl(this, mHandler);
//		cornerstoneControl.onSuccess();
		int channelNum = DBOperator.getChannelNum();

		for(int i=0;i<channelNum;i++)
		{
			infos.add(""+i);
		}


//		Message msg = Message.obtain();
//		msg.obj = infos;
//		msg.what = 0;
//		mHandler.sendMessage(msg);
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		try {



			unsubscribed.clear();
			subscribed.clear();

			channels = DBOperator.getChannels();
			sort(channels);

			ArrayList<DiscoveryChannel> unsub = new ArrayList<DiscoveryChannel>();
			ArrayList<DiscoveryChannel> sub = new ArrayList<DiscoveryChannel>();
			for (int i = 0; i < channels.size(); i++) {
				if (channels.get(i).isSubscriptionState())
					sub.add(channels.get(i));
				else
					unsub.add(channels.get(i));
			}
			unsubscribed.addAll(unsub);
			subscribed.addAll(sub);
//
//			((featuredAdapter) storyList.getAdapter()).getData().addAll(unsubscribed);
//			((featuredAdapter) subscriptionList.getAdapter()).getData().addAll(subscribed);
			featureList.setAdapter(new featuredAdapter(this,unsub));
			subscriptionList.setAdapter(new featuredAdapter(this,sub));



			int size = unsubscribed.size();
			int length = 100;
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			float density = dm.density;
			int gridviewWidth = (int) (size * (length + 4) * density);
			int itemWidth = (int) (length * density);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
			featureList.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
			featureList.setColumnWidth(itemWidth); // 设置列表项宽
			featureList.setHorizontalSpacing(5); // 设置列表项水平间距
			featureList.setStretchMode(GridView.NO_STRETCH);
			featureList.setNumColumns(size); // 设置列数量=列表集合数

			setListViewHeightBasedOnChildren(subscriptionList);

			((featuredAdapter) featureList.getAdapter()).notifyDataSetChanged();
			((featuredAdapter) subscriptionList.getAdapter()).notifyDataSetChanged();
			featureList.invalidateViews();
			featureList.refreshDrawableState();
			subscriptionList.invalidateViews();
			subscriptionList.refreshDrawableState();

			if(unsubscribed.size()==0) {
				featuredText.setVisibility(View.INVISIBLE);
				featureList.setVisibility(View.INVISIBLE);
			}
			else {
				featuredText.setVisibility(View.VISIBLE);
				featureList.setVisibility(View.VISIBLE);
			}

			if(subscribed.size()==0) {
				rootSubscriptionText.setVisibility(View.INVISIBLE);
				subscriptionList.setVisibility(View.INVISIBLE);
			}
			else {
				rootSubscriptionText.setVisibility(View.VISIBLE);
				subscriptionList.setVisibility(View.VISIBLE);
			}

		}catch(Exception e)
		{
			e.printStackTrace();
			Log.e("story activity",e.getMessage());
		}

	}

	private void init() {

		channels = DBOperator.getChannels();


		sort(channels);

		frame = (ScrollView) findViewById(R.id.story_form);
		frame.setOnTouchListener(new OnSwipeTouchListener(this.getBaseContext(),StoryActivity.this){
		});
		unsubscribed = new ArrayList<DiscoveryChannel>();
		subscribed = new ArrayList<DiscoveryChannel>();
//		exchangeChannel(channels.get(0), channels.get(1));

		for(int i=0;i<channels.size();i++)
		{
			if(channels.get(i).isSubscriptionState())
				subscribed.add(channels.get(i));
			else
				unsubscribed.add(channels.get(i));
		}

		searchView = (SearchView) findViewById(R.id.searchView);
		searchButton = (ImageButton) findViewById(R.id.search_button);
		discoverButton = (ImageButton) findViewById(R.id.discover_button);

		Log.e("story activity","storyList");
		storyList = (ExpandableListView) findViewById(R.id.storyList);

		Log.e("story activity","friendStoryList");
		friendStoryList = (ListView) findViewById(R.id.friendStoryList);

		Log.e("story activity","featureList");
		featureList = (GridView) findViewById(R.id.featureList);
		subscriptionList = (ListView)findViewById(R.id.subscriptionList);

		rootSubscriptionText = (TextView) findViewById(R.id.rootSubscriptionText);
		featuredText = (TextView) findViewById(R.id.featuredText);
		friendStoryText = (TextView) findViewById(R.id.friendStoryText);

		if(unsubscribed.size()==0) {
			featuredText.setVisibility(View.INVISIBLE);
			featureList.setVisibility(View.INVISIBLE);
		}
		else {
			featuredText.setVisibility(View.VISIBLE);
			featureList.setVisibility(View.VISIBLE);
		}

		if(subscribed.size()==0) {
			rootSubscriptionText.setVisibility(View.INVISIBLE);
			subscriptionList.setVisibility(View.INVISIBLE);
		}
		else {
			rootSubscriptionText.setVisibility(View.VISIBLE);
			subscriptionList.setVisibility(View.VISIBLE);
		}


		searchFriendButton = (Button) findViewById(R.id.searchFriendButton);
		ChatButton = (ImageButton) findViewById(R.id.ChatButton);
		CameraButton = (ImageButton) findViewById(R.id.CamaraButton);
		StoryButton = (ImageButton) findViewById(R.id.StoryButton);

		Log.e("story activity","initAdapter");
		initAdapter(subscribed,unsubscribed);
		initListener();
	}

	private void initAdapter(ArrayList<DiscoveryChannel> subscribed, ArrayList<DiscoveryChannel> unsubscribed) {

		int size = unsubscribed.size();
		int length = 100;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int gridviewWidth = (int) (size * (length + 4) * density);
		int itemWidth = (int) (length * density);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
		featureList.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
		featureList.setColumnWidth(itemWidth); // 设置列表项宽
		featureList.setHorizontalSpacing(5); // 设置列表项水平间距
		featureList.setStretchMode(GridView.NO_STRETCH);
		featureList.setNumColumns(size); // 设置列数量=列表集合数

		featureList.setAdapter(new featuredAdapter(this, unsubscribed));

		ArrayList<FriendStory> myStory = new ArrayList<FriendStory>();
		FriendStory mystory = new FriendStory("My Story");
		mystory.addSnap("my snap1");
		mystory.addSnap("my snap2");
		mystory.addSnap("my snap3");
		myStory.add(mystory);




		storyList.setAdapter(new MyStoryListAdapter(this, myStory));
		setListViewHeightBasedOnChildren(storyList);

		ArrayList<FriendStory> stories = new ArrayList<FriendStory>();
		stories.add(new FriendStory("test1"));
		stories.add(new FriendStory("test2"));
		stories.add(new FriendStory("test3"));

		friendStoryList.setAdapter(new StoryListAdapter(this, stories));
		setListViewHeightBasedOnChildren(friendStoryList);

		subscriptionList.setAdapter(new featuredAdapter(this, subscribed));
		setListViewHeightBasedOnChildren(subscriptionList);




	}


	private void initListener()
	{
		Log.e("initListener","start");

		try {
			Log.e("initListener", "searchButton");
			searchButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					String email = searchView.getText().toString();
					Log.e("Search user Email", "search function");
				}
			});

			Log.e("initListener", "discoverButton");
			discoverButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(StoryActivity.this, DiscoverActivity.class);
					startActivity(intent);
				}
			});

			Log.e("initListener", "searchFriendButton");
			searchFriendButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.e("Button action", "turn to search friend activity");
//				Intent intent = new Intent(StoryActivity.this, DiscoverActivity.class);
//				startActivity(intent);
				}
			});

			Log.e("initListener", "ChatButton");
			ChatButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.e("Button action", "turn to chat activity");
//				Intent intent = new Intent(StoryActivity.this, DiscoverActivity.class);
//				startActivity(intent);
				}
			});

			Log.e("initListener", "CameraButton");
			CameraButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.e("Button action", "turn to camera activity");
//				Intent intent = new Intent(StoryActivity.this, CameraActivity.class);
//				startActivity(intent);
				}
			});

			Log.e("initListener", "StoryButton");
			StoryButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.e("Button action", "turn to story activity");
//				Intent intent = new Intent(StoryActivity.this, DiscoverActivity.class);
//				startActivity(intent);
				}
			});

			Log.e("initListener", "featureList");
			featureList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
//                // When clicked, show a toast with the TextView text
//                TextView tv = (TextView)view.findViewById(R.id.userName);
//                Toast.makeText(getApplicationContext(), tv.getText(), Toast.LENGTH_SHORT).show();

					GridView channelList = (GridView) parent;
					DiscoveryChannel channel = (DiscoveryChannel) channelList.getItemAtPosition(position);

					DBOperator.visit(channel.getChannelId());
					Intent intent = new Intent(StoryActivity.this, DiscoverChannelActivity.class);
					intent.putExtra("channelId", channel.getChannelId());
					intent.putExtra("subscription", channel.isSubscriptionState());
					intent.putExtra("channelName", channel.getName());
					intent.putExtra("contents", channel.getContents());
//					intent.putExtra("profile", channels.get(position).getProfile());
					try {
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}


				}
			});

			Log.e("initListener", "subscriptionList");
			subscriptionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {

					try {
						ListView channelList = (ListView) parent;
						DiscoveryChannel channel = (DiscoveryChannel) channelList.getItemAtPosition(position);

						DBOperator.visit(channel.getChannelId());
						Intent intent = new Intent(StoryActivity.this, DiscoverChannelActivity.class);
						intent.putExtra("channelId", channel.getChannelId());
						intent.putExtra("subscription", channel.isSubscriptionState());
						intent.putExtra("channelName", channel.getName());
						intent.putExtra("contents", channel.getContents());
//					intent.putExtra("profile", channels.get(position).getProfile());

						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
						Log.e("sub item error", e.getMessage());
					}


				}
			});

			Log.e("initListener", "expand listener");
			storyList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

				@Override
				public void onGroupExpand(int groupPosition) {
                /*
                 * Log.e("expand", "扩展"); for (int i = 0; i < 4; i++) { if
                 * ((groupPosition != i) && listView.isGroupExpanded(i)) {
                 * listView.collapseGroup(i); } }
                 */
					/**
					 * 计算group下的子项的高度
					 */
					setExpandedListViewHeightBasedOnChildren(storyList,
							groupPosition);
					// 更新group每一项的高度
					setListViewHeightBasedOnChildren(
							storyList);
				}
			});


			Log.e("initListener", "collapsel listener");
			storyList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

				@Override
				public void onGroupCollapse(int groupPosition) {
					Log.e("collapse", "收缩");

                /*
                 * 计算group下的每一个子项的高度，然后收缩
                 */
					setCollapseListViewHeightBasedOnChildren(storyList,
							groupPosition);
                /*
                 * 重新评估group的高度
                 */
					setListViewHeightBasedOnChildren(
							storyList);
                /*
                 * ListUtil.setCollapseListViewHeightBasedOnChildren(listView,
                 * groupPosition);
                 */
				}
			});


			Log.e("initListener", "friendStoryList");
			friendStoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {

					try {
						Log.e("story clicked", "" + position);

						ListView storyList = (ListView) parent;
						FriendStory story = (FriendStory) storyList.getItemAtPosition(position);

						Log.e("story read", story.getName());
						Log.e("story read", ""+story.getVisitNum());

						story.visit();

						if(story.getVisitNum()>=2) {
							((StoryListAdapter) friendStoryList.getAdapter()).list.remove(story);
							setListViewHeightBasedOnChildren(friendStoryList);
						}

						if(((StoryListAdapter) friendStoryList.getAdapter()).list.size()==0)
							friendStoryText.setVisibility(View.INVISIBLE);

						((StoryListAdapter) friendStoryList.getAdapter()).notifyDataSetChanged();


					} catch (Exception e) {
						e.printStackTrace();
						Log.e("sub item error", e.getMessage());
					}


				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Log.e("initListener error",e.getMessage());
		}


		Log.e("initListener","finished");
	}

	private void sort (ArrayList<DiscoveryChannel> channels)
	{
		Object[] objects = channels.toArray();
		DiscoveryChannel[] channelArray = new DiscoveryChannel[objects.length];
		for(int i = (channelArray.length-1); i >= 0; i--)
			channelArray[i] = (DiscoveryChannel)objects [i];

		for (int i = (channelArray.length-1); i >= 0; i--) {
			for (int j = 1; j <= i; j++) {
				if(channelArray[j].getChannelId() < channelArray[j-1].getChannelId())
				{
					DiscoveryChannel temp = channelArray[j-1];
					channelArray[j-1] = channelArray[j];
					channelArray[j] = temp;
				}
			}
		}

		for (int i = (channelArray.length-1); i >= 0; i--) {
			for (int j = 1; j <= i; j++) {
				if (channelArray[j].getVisitNum() > channelArray[j - 1].getVisitNum()) {
					DiscoveryChannel temp = channelArray[j - 1];
					channelArray[j - 1] = channelArray[j];
					channelArray[j] = temp;
				}
			}
		}

		for (int i = (channelArray.length-1); i >= 0; i--) {
			for (int j = 1; j <= i; j++) {
				if (channelArray[j].isSubscriptionState() && (!channelArray[j-1].isSubscriptionState()))
				{
					DiscoveryChannel temp = channelArray[j-1];
					channelArray[j-1] = channelArray[j];
					channelArray[j] = temp;
				}
			}
		}




		channels.clear();
		for(int i = 0; i < channelArray.length; i++)
			channels.add(channelArray[i]);
	}


	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public static void setExpandedListViewHeightBasedOnChildren(
			ExpandableListView listView, int groupPosition) {
		ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
		if (listAdapter == null) {
			return;
		}
		View listItem = listAdapter.getChildView(groupPosition, 0, true, null,
				listView);
		listItem.measure(0, 0);
		int appendHeight = 0;
		for (int i = 0; i < listAdapter.getChildrenCount(groupPosition); i++) {
			appendHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
//        Log.d(TAG, "Expand params.height" + params.height);
		params.height += appendHeight;
		listView.setLayoutParams(params);
	}

	public static void setCollapseListViewHeightBasedOnChildren(
			ExpandableListView listView, int groupPosition) {
		ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
		if (listAdapter == null) {
			return;
		}
		View listItem = listAdapter.getChildView(groupPosition, 0, true, null,
				listView);
		listItem.measure(0, 0);
		int appendHeight = 0;
		for (int i = 0; i < listAdapter.getChildrenCount(groupPosition); i++) {
			appendHeight += listItem.getMeasuredHeight();
		}
        /*Log.d(TAG,
                "Collapse childCount="
                        + listAdapter.getChildrenCount(groupPosition));*/
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height -= appendHeight;
		listView.setLayoutParams(params);
	}


}
