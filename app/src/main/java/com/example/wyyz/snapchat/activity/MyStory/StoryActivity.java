package com.example.wyyz.snapchat.activity.MyStory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.wyyz.snapchat.activity.DisplaySnapActivity;
import com.example.wyyz.snapchat.activity.OpenMySnapActivity;
import com.example.wyyz.snapchat.activity.PreviewActivity;
import com.example.wyyz.snapchat.db.DataBaseOperator;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.DiscoveryChannel;
import com.example.wyyz.snapchat.model.FriendStory;
import com.example.wyyz.snapchat.model.FriendStorySnap;
import com.example.wyyz.snapchat.model.MyStorySnap;
import com.example.wyyz.snapchat.model.User;
import com.example.wyyz.snapchat.util.OnSwipeTouchListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class StoryActivity extends Activity {
	public static final String TAG = "StoryActivity";

	private ArrayList<DiscoveryChannel> unsubscribed;
	private ArrayList<DiscoveryChannel> subscribed;

	//	private FinalBitmap fb;
	private List<String> infos = new ArrayList<String>();
	private List<Point> lostPoint = new ArrayList<Point>();// 用于记录空白块的位置
//	private int currentPage = 1;

	private FirebaseAuth mAuth;
	private ScrollView frame;
	private ListView subscriptionList;
	private SearchView searchView;
	private ImageButton searchButton;
	private ImageButton discoverButton;
	private ExpandableListView storyList;
	private ListView friendStoryList;
	private ListView allStoryList;
	private GridView featureList;
	private TextView rootSubscriptionText;
	private TextView featuredText;
	private TextView friendStoryText;
	private Button searchFriendButton;
	private ImageButton ChatButton;
	private ImageButton CameraButton;
	private ImageButton StoryButton;
	private final ArrayList<ArrayList<FriendStorySnap>> stories  = new ArrayList<ArrayList<FriendStorySnap>>();
	private final ArrayList<ArrayList<FriendStorySnap>> allStories  = new ArrayList<ArrayList<FriendStorySnap>>();
	private ArrayList<FriendStory> myStory;



	private DataBaseOperator DBOperator;
	private ArrayList<DiscoveryChannel> channels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.avtivity_story);

		DBOperator = new DataBaseOperator(this);
		DBOperator.initialise();


		try{
			init();}
		catch(Exception e)
		{
			Log.e("error",e.getMessage());
			e.printStackTrace();}
		Log.e("state","finish init");

		int channelNum = DBOperator.getChannelNum();

		for(int i=0;i<channelNum;i++)
		{
			infos.add(""+i);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		try {

//load the data

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
			//set params to resize the view
			featureList.setLayoutParams(params);
			featureList.setColumnWidth(itemWidth);
			featureList.setHorizontalSpacing(5);
			featureList.setStretchMode(GridView.NO_STRETCH);
			featureList.setNumColumns(size);

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

	private ArrayList<MyStorySnap> getMyStory()
	{
		ArrayList<MyStorySnap> myStory = new ArrayList<MyStorySnap>();
		String userId = mAuth.getInstance().getCurrentUser().getUid();
		myStory = SnapChatDB.getInstance(StoryActivity.this).getMyStory(userId);

		return myStory;
	}
//init the components
	private void init() {

		channels = DBOperator.getChannels();

		//sort the channels
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
		allStoryList = (ListView) findViewById(R.id.allStoryList);

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
//init the adapters
	private void initAdapter(ArrayList<DiscoveryChannel> subscribed, ArrayList<DiscoveryChannel> unsubscribed) {

		int size = unsubscribed.size();
		int length = 100;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int gridviewWidth = (int) (size * (length + 4) * density);
		int itemWidth = (int) (length * density);

		//set params to resize the view
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
		featureList.setLayoutParams(params);
		featureList.setColumnWidth(itemWidth);
		featureList.setHorizontalSpacing(5);
		featureList.setStretchMode(GridView.NO_STRETCH);
		featureList.setNumColumns(size);

		featureList.setAdapter(new featuredAdapter(this, unsubscribed));

		myStory = new ArrayList<FriendStory>();
		FriendStory mystory = new FriendStory("My Story");
		mystory.addSnaps(getMyStory());

		myStory.add(mystory);



//build the mystory list and change it size adaptingto its item number
		storyList.setAdapter(new MyStoryListAdapter(this, myStory));
		setListViewHeightBasedOnChildren(storyList);


//		stories.add(new FriendStory("test1"));
//		stories.add(new FriendStory("test2"));
//		stories.add(new FriendStory("test3"));
		fetchFriendStory(stories);
		friendStoryList.setAdapter(new newStoryListAdapter(this, stories));
		setListViewHeightBasedOnChildren(friendStoryList);

		//build the all stories list
		fetchAllStory(allStories);
		allStoryList.setAdapter(new allStoryListAdapter(this, allStories));
		setListViewHeightBasedOnChildren(allStoryList);

		subscriptionList.setAdapter(new featuredAdapter(this, subscribed));
		setListViewHeightBasedOnChildren(subscriptionList);




	}

//init listener
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
					try {

						Log.e("stories size",""+stories.size());
						for(int i =0;i<stories.size();i++)
							Log.e("story size "+i,""+stories.get(i).size());

					}
					catch(Exception e)
					{
						Log.e("error", e.getMessage());
						e.printStackTrace();
					}

				}
			});

			Log.e("initListener", "ChatButton");
			ChatButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.e("Button action", "turn to chat activity");

				}
			});

			Log.e("initListener", "CameraButton");
			CameraButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.e("Button action", "turn to camera activity");

				}
			});

			Log.e("initListener", "StoryButton");
			StoryButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.e("Button action", "turn to story activity");
				}
			});

			//turn to the discover activity
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

			//turn to the subscribed discover channel
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


//add child listener to the expandable list
			storyList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
											int groupPosition, int childPosition, long id) {
//					ExpandableListView storyList = (ExpandableListView) parent;
					try {
						Log.e("group", "" + groupPosition);
						Log.e("child", "" + childPosition);
						MyStorySnap snap = myStory.get(groupPosition).getSnaps().get(childPosition);
						Log.e("path", snap.getPath());
						Log.e("timer", "" + snap.getTimingOut());
						Intent intent = new Intent(StoryActivity.this,DisplaySnapActivity.class);
						ArrayList<String> str = new ArrayList<String>();

							str.add(snap.getPath());

						int[] timer = new int[1];
							timer[0] = snap.getTimingOut();


						intent.putExtra("SnapPath",str);
						intent.putExtra("Timer",timer);
						intent.putExtra("ActivityName","StoryActivity");
						startActivity(intent);
					}catch(Exception e)
					{
						Log.e("error",e.getMessage());
					}

					return true;
				}
			});

//expand the list
			Log.e("initListener", "expand listener");
			storyList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

				@Override
				public void onGroupExpand(int groupPosition) {

					setExpandedListViewHeightBasedOnChildren(storyList,
							groupPosition);

					setListViewHeightBasedOnChildren(
							storyList);
				}
			});

//collpase the list
			Log.e("initListener", "collapsel listener");
			storyList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

				@Override
				public void onGroupCollapse(int groupPosition) {
					Log.e("collapse", "collapse");

					setCollapseListViewHeightBasedOnChildren(storyList,
							groupPosition);


					setListViewHeightBasedOnChildren(
							storyList);

				}
			});


			allStoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {

					try {
						Log.e("story clicked", "" + position);

						ListView storyList = (ListView) parent;
						ArrayList<FriendStorySnap> story = (ArrayList<FriendStorySnap>) storyList.getItemAtPosition(position);

						Log.e("story read", story.get(0).getUserName());

						Intent intent = new Intent(StoryActivity.this,DisplaySnapActivity.class);
						ArrayList<String> str = new ArrayList<String>();
						for(FriendStorySnap snap: story) {
							str.add(snap.getPath());
							Log.e("path",snap.getPath());
						}
						int[] timer = new int[story.size()];
						for(int i=0;i<story.size();i++) {
							timer[i] = story.get(i).getTimingOut();
							Log.e("timer",""+timer[i]);
						}

						intent.putExtra("SnapPath",str);
						intent.putExtra("Timer",timer);
						intent.putExtra("ActivityName","StoryActivity");

						for(FriendStorySnap snap: story)
							snap.visit();

						startActivity(intent);

						((allStoryListAdapter) allStoryList.getAdapter()).notifyDataSetChanged();


					} catch (Exception e) {
						e.printStackTrace();
						Log.e("sub item error", e.getMessage());
					}


				}
			});



			Log.e("initListener", "friendStoryList");
			friendStoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {

					try {
						Log.e("story clicked", "" + position);

						ListView storyList = (ListView) parent;
						ArrayList<FriendStorySnap> story = (ArrayList<FriendStorySnap>) storyList.getItemAtPosition(position);

						Log.e("story read", story.get(0).getUserName());

						Intent intent = new Intent(StoryActivity.this,DisplaySnapActivity.class);
						ArrayList<String> str = new ArrayList<String>();
						for(FriendStorySnap snap: story) {
							str.add(snap.getPath());
							Log.e("path",snap.getPath());
						}
						int[] timer = new int[story.size()];
						for(int i=0;i<story.size();i++) {
							timer[i] = story.get(i).getTimingOut();
							Log.e("timer",""+timer[i]);
						}

						intent.putExtra("SnapPath",str);
						intent.putExtra("Timer",timer);
						intent.putExtra("ActivityName","StoryActivity");

						for(FriendStorySnap snap: story)
							snap.visit();

						startActivity(intent);

						boolean remove = true;
						for(FriendStorySnap snap: story)
							if(snap.getVisitNum()<2) {
								remove = false;
							}

						if(remove) {
							((newStoryListAdapter) friendStoryList.getAdapter()).list.remove(story);
							setListViewHeightBasedOnChildren(friendStoryList);
						}

						if(((newStoryListAdapter) friendStoryList.getAdapter()).list.size()==0)
							friendStoryText.setVisibility(View.INVISIBLE);

						((newStoryListAdapter) friendStoryList.getAdapter()).notifyDataSetChanged();


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
//sort the channel based on the subscription and visit num
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

//fetch recent data from the firebase
	private void fetchFriendStory(final ArrayList<ArrayList<FriendStorySnap>> stories) {

		stories.clear();
		final String userId = mAuth.getInstance().getCurrentUser().getUid();
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		final DatabaseReference ref = database.getReference();

		DatabaseReference friendRef = ref.child("Users").child(userId).child("friends");
//		Log.e("read friend",friendRef.toString());
		friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
//				Log.e("read friend","start");
				if (dataSnapshot.getValue() != null) {
//					Log.d("friendDataSnap", dataSnapshot.getValue().toString());
					Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
					Set<String> friendIdSet = new HashSet<String>();
					Iterator<Map.Entry<String, Object>> entries = objectMap.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<String, Object> entry = entries.next();
						friendIdSet.add(entry.getKey());
					}

					//get user instance of friend
//					Log.e("friend size",""+friendIdSet.size());
					for (final String friendid : friendIdSet) {


//						Log.e("friendSet", friendid);
						DatabaseReference friendStoryRef = ref.child("MyStory").child(friendid);
						DatabaseReference friendStoryRecordRef = ref.child("MyStoryVisitRecord").child(friendid);






						friendStoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(DataSnapshot dataSnapshot) {
//								Log.e("read snaps","start");
								if (dataSnapshot.getValue() != null) {
									Log.e("friend available",""+friendid);
									final ArrayList<FriendStorySnap> story = new ArrayList<FriendStorySnap>();
									stories.add(story);
//									Log.e("friendDataSnap", dataSnapshot.getValue().toString());
									Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
									Set<String> friendStoryIdSet = new HashSet<String>();
									Iterator<Map.Entry<String, Object>> entries = objectMap.entrySet().iterator();

									while (entries.hasNext()) {
										Map.Entry<String, Object> entry = entries.next();
										friendStoryIdSet.add(entry.getKey());
//										Log.e("friendStoryIdSet entry",entry.getKey());
									}

									//get user instance of friend
									for (final String friendStoryId : friendStoryIdSet) {
//										Log.e("friendStorySet", friendStoryId);
										DatabaseReference friendStoryRef = ref.child("MyStory").child(friendid);
										friendStoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
											@Override
											public void onDataChange(DataSnapshot dataSnapshot) {
//												Log.e("read snap", "start");
												if (dataSnapshot.getValue() != null) {

													final int timingout =  Integer.parseInt(dataSnapshot.child(friendStoryId).child("timingout").getValue().toString());
													final String timeStamp = dataSnapshot.child(friendStoryId).child("timeStamp").getValue().toString();
													final String url = dataSnapshot.child(friendStoryId).child("url").getValue().toString();

													DatabaseReference friendStoryRef = ref.child("MyStoryVisitRecord").child(friendid).child(friendStoryId).child(userId);
//													Log.e("check ref", friendStoryRef.toString());
													friendStoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
														@Override
														public void onDataChange(DataSnapshot dataSnapshot) {

															if (dataSnapshot.getValue() != null) {
//																Log.e("read snapvisit", "start");
																final int visitNum =  Integer.parseInt(dataSnapshot.child("visitNum").getValue().toString());
																DatabaseReference friendStoryRef = ref.child("Users").child(friendid);
//																Log.e("fetch user", friendStoryRef.toString());
																friendStoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
																	@Override
																	public void onDataChange(DataSnapshot dataSnapshot) {
//																		Log.e("read user", "start");
																		if (dataSnapshot.getValue() != null) {
																			final String userName =  dataSnapshot.child("username").getValue().toString();

																			Log.e("userId",friendid);
																			Log.e("userName",userName);
																			Log.e("timeStamp",timeStamp);
																			Log.e("timingout",""+timingout);
																			Log.e("visitNum",""+visitNum);
																			FriendStorySnap friendStorySnap= new FriendStorySnap();
																			friendStorySnap.setTimingOut(timingout);
																			friendStorySnap.setTimestamp(timeStamp);
																			friendStorySnap.setPath(url);
																			friendStorySnap.setUserId(friendid);
																			friendStorySnap.setUserName(userName);
																			friendStorySnap.setVisitNum(visitNum);
																			Date date = Calendar.getInstance().getTime();
																			Date snapDate = friendStorySnap.getTimestamp();
																			long day = date.getTime() / (24*60*60*1000) - snapDate.getTime() / (24*60*60*1000);
																			if((day<=1)&&(friendStorySnap.getVisitNum()<2)) {
																				story.add(friendStorySnap);
																				((newStoryListAdapter) friendStoryList.getAdapter()).notifyDataSetChanged();
																			}

																		}
																	}

																	@Override
																	public void onCancelled(DatabaseError databaseError) {
																	}
																});
															}
														}

														@Override
														public void onCancelled(DatabaseError databaseError) {
														}
													});
//													stories.add(story);
												}
											}

											@Override
											public void onCancelled(DatabaseError databaseError) {
											}
										});

//										DatabaseReference friendStoryRecordRef = ref.child("MyStoryVisitRecord").child(uid).child(Snapname);
//
//										Map<String, Object> updates = new HashMap<String, Object>();
//										updates.put("visitNum", 0);
//										myStoryRecordRef.updateChildren(updates);

									}

								}
							}

							@Override
							public void onCancelled(DatabaseError databaseError) {

							}
						});


					}
					Log.e("length of stories",""+stories.size());
					for(int i=0;i<stories.size();i++)
						Log.e("length of storie "+i,""+stories.get(i).size());
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}



// fetch all data within a day from firebase
	private void fetchAllStory(final ArrayList<ArrayList<FriendStorySnap>> stories) {

		stories.clear();
		final String userId = mAuth.getInstance().getCurrentUser().getUid();
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		final DatabaseReference ref = database.getReference();

		DatabaseReference friendRef = ref.child("Users").child(userId).child("friends");
//		Log.e("read friend",friendRef.toString());
		friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
//				Log.e("read friend","start");
				if (dataSnapshot.getValue() != null) {
//					Log.d("friendDataSnap", dataSnapshot.getValue().toString());
					Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
					Set<String> friendIdSet = new HashSet<String>();
					Iterator<Map.Entry<String, Object>> entries = objectMap.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<String, Object> entry = entries.next();
						friendIdSet.add(entry.getKey());
					}

					//get user instance of friend
//					Log.e("friend size",""+friendIdSet.size());
					for (final String friendid : friendIdSet) {


//						Log.e("friendSet", friendid);
						DatabaseReference friendStoryRef = ref.child("MyStory").child(friendid);
						DatabaseReference friendStoryRecordRef = ref.child("MyStoryVisitRecord").child(friendid);






						friendStoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(DataSnapshot dataSnapshot) {
//								Log.e("read snaps","start");
								if (dataSnapshot.getValue() != null) {
									Log.e("friend available",""+friendid);
									final ArrayList<FriendStorySnap> story = new ArrayList<FriendStorySnap>();
									stories.add(story);
//									Log.e("friendDataSnap", dataSnapshot.getValue().toString());
									Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
									Set<String> friendStoryIdSet = new HashSet<String>();
									Iterator<Map.Entry<String, Object>> entries = objectMap.entrySet().iterator();

									while (entries.hasNext()) {
										Map.Entry<String, Object> entry = entries.next();
										friendStoryIdSet.add(entry.getKey());
//										Log.e("friendStoryIdSet entry",entry.getKey());
									}

									//get user instance of friend
									for (final String friendStoryId : friendStoryIdSet) {
//										Log.e("friendStorySet", friendStoryId);
										DatabaseReference friendStoryRef = ref.child("MyStory").child(friendid);
										friendStoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
											@Override
											public void onDataChange(DataSnapshot dataSnapshot) {
//												Log.e("read snap", "start");
												if (dataSnapshot.getValue() != null) {

													final int timingout =  Integer.parseInt(dataSnapshot.child(friendStoryId).child("timingout").getValue().toString());
													final String timeStamp = dataSnapshot.child(friendStoryId).child("timeStamp").getValue().toString();
													final String url = dataSnapshot.child(friendStoryId).child("url").getValue().toString();

													DatabaseReference friendStoryRef = ref.child("MyStoryVisitRecord").child(friendid).child(friendStoryId).child(userId);
//													Log.e("check ref", friendStoryRef.toString());
													friendStoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
														@Override
														public void onDataChange(DataSnapshot dataSnapshot) {

															if (dataSnapshot.getValue() != null) {
//																Log.e("read snapvisit", "start");
																final int visitNum =  Integer.parseInt(dataSnapshot.child("visitNum").getValue().toString());
																DatabaseReference friendStoryRef = ref.child("Users").child(friendid);
//																Log.e("fetch user", friendStoryRef.toString());
																friendStoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
																	@Override
																	public void onDataChange(DataSnapshot dataSnapshot) {
//																		Log.e("read user", "start");
																		if (dataSnapshot.getValue() != null) {
																			final String userName =  dataSnapshot.child("username").getValue().toString();

																			Log.e("userId",friendid);
																			Log.e("userName",userName);
																			Log.e("timeStamp",timeStamp);
																			Log.e("timingout",""+timingout);
																			Log.e("visitNum",""+visitNum);
																			FriendStorySnap friendStorySnap= new FriendStorySnap();
																			friendStorySnap.setTimingOut(timingout);
																			friendStorySnap.setTimestamp(timeStamp);
																			friendStorySnap.setPath(url);
																			friendStorySnap.setUserId(friendid);
																			friendStorySnap.setUserName(userName);
																			friendStorySnap.setVisitNum(visitNum);
																			Date date = Calendar.getInstance().getTime();
																			Date snapDate = friendStorySnap.getTimestamp();
																			long day = date.getTime() / (24*60*60*1000) - snapDate.getTime() / (24*60*60*1000);
																			if((day<=1)&&(friendStorySnap.getVisitNum()>=2)) {
																				story.add(friendStorySnap);
																				((allStoryListAdapter) allStoryList.getAdapter()).notifyDataSetChanged();
																			}

																		}
																	}

																	@Override
																	public void onCancelled(DatabaseError databaseError) {
																	}
																});
															}
														}

														@Override
														public void onCancelled(DatabaseError databaseError) {
														}
													});
//													stories.add(story);
												}
											}

											@Override
											public void onCancelled(DatabaseError databaseError) {
											}
										});

//										DatabaseReference friendStoryRecordRef = ref.child("MyStoryVisitRecord").child(uid).child(Snapname);
//
//										Map<String, Object> updates = new HashMap<String, Object>();
//										updates.put("visitNum", 0);
//										myStoryRecordRef.updateChildren(updates);

									}

								}
							}

							@Override
							public void onCancelled(DatabaseError databaseError) {

							}
						});


					}
					Log.e("length of stories",""+stories.size());
					for(int i=0;i<stories.size();i++)
						Log.e("length of storie "+i,""+stories.get(i).size());
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}


//resize the listview to adapt to its items
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
//resize the expandable listview
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
