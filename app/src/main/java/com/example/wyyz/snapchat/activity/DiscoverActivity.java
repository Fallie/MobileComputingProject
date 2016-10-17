package com.example.wyyz.snapchat.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.customView.LazyScrollView;
import com.example.wyyz.snapchat.customView.Rotate3dAnimation;
import com.example.wyyz.snapchat.db.DataBaseOperator;
import com.example.wyyz.snapchat.model.DiscoveryChannel;
import com.example.wyyz.snapchat.util.OnSwipeTouchListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * dynamic layout
 * @author wy
 */
public class DiscoverActivity extends Activity {
	public static final String TAG = "DiscoverActivity";
	private static final int COLUMNCOUNT = 2;//column
	private int columnWidth = 250;// width of item
	private int itemHeight = 0;
	private int rowCountPerScreen = 2;
	private int cols = 2;// total column
	private ArrayList<Integer> colYs = new ArrayList<Integer>();
//	private ArrayList<View> currentViews = new ArrayList<View>();
	private LayoutInflater mInflater;
	private RelativeLayout rootView;
//	private FinalBitmap fb;
	private List<String> infos = new ArrayList<String>();
	private List<Point> lostPoint = new ArrayList<Point>();
//	private int currentPage = 1;
	private LazyScrollView rootScroll;
	private RelativeLayout loading_rl;
	private FrameLayout frame;

	private DataBaseOperator DBOperator;
	private ArrayList<DiscoveryChannel> channels;

	private ArrayList<ArrayList<String>> contents = new ArrayList<ArrayList<String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_cornerstone);

		DBOperator = new DataBaseOperator(this);
		DBOperator.initialise();
		DBOperator.update("leif@gmail.com");

		init();
//		CornerstoneControl cornerstoneControl = new CornerstoneControl(this, mHandler);
//		cornerstoneControl.onSuccess();
		int channelNum = DBOperator.getChannelNum();

		for(int i=0;i<channelNum;i++)
		{
			infos.add(""+i);
		}


		Message msg = Message.obtain();
		msg.obj = infos;
		msg.what = 0;
		mHandler.sendMessage(msg);
	}
	@SuppressWarnings("deprecation")
	private void init() {
//init the ui componet
		channels = DBOperator.getChannels();



		sort(channels);
//		exchangeChannel(channels.get(0), channels.get(1));

		for(int i=0;i<channels.size();i++)
			Log.e("exchanged channels id",""+channels.get(i).getChannelId());
//		Log.e("channels size",""+channels.size());


		rootView = (RelativeLayout) this.findViewById(R.id.rootView);
		rootView.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
		rootScroll = (LazyScrollView) this.findViewById(R.id.rootScroll);
//		rootView.setOnTouchListener(new OnSwipeTouchListener(this.getBaseContext(),DiscoverActivity.this){
//		});
//		rootScroll.setOnScrollListener(this);
		rootScroll.getView();
//		rootScroll.setOnTouchListener(new OnSwipeTouchListener(this.getBaseContext(),DiscoverActivity.this){
//		});
		mInflater = getLayoutInflater();
		Display display = getWindowManager().getDefaultDisplay();
		
		int width = display.getWidth();
		int height = display.getHeight();
		Configuration cf = this.getResources().getConfiguration();

		if (cf.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			rowCountPerScreen = 3;
		} else {
			rowCountPerScreen = 6;
		}
		columnWidth = width / COLUMNCOUNT;
		itemHeight = height / rowCountPerScreen;
//		fb = FinalBitmap.create(this);
		for (int i = 0; i < 2; i++) {
			colYs.add(0);
		}
		loading_rl = (RelativeLayout) this.findViewById(R.id.loading_rl);
		loading_rl.setVisibility(View.VISIBLE);
//		loading_rl.setOnTouchListener(new OnSwipeTouchListener(this.getBaseContext(),DiscoverActivity.this){
//		});
	}
// sort the channel based on user's historical visit record and subscription
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

	//add view of channel
	
	private synchronized void addView(View view, final int position) {
		placeBrick(view);
		ImageView picView = (ImageView) view.findViewById(R.id.imageView);
		rootView.addView(view);
		picView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e("MainActivity", "position:" + position);
//				if(position == 0) {
//					showChildQuickActionBar(v);
//				} else {
				DBOperator.visit(channels.get(position).getChannelId());
					Intent intent = new Intent(DiscoverActivity.this, DiscoverChannelActivity.class);
					intent.putExtra("channelId", channels.get(position).getChannelId());
					intent.putExtra("subscription", channels.get(position).isSubscriptionState());
					intent.putExtra("channelName", channels.get(position).getName());
					intent.putExtra("contents", channels.get(position).getContents());
//					intent.putExtra("profile", channels.get(position).getProfile());
					startActivity(intent);
//				}
			}
		});

		picView.setOnTouchListener(new OnSwipeTouchListener(this.getBaseContext(),DiscoverActivity.this){
		});
		startAnim(view);

		Resources res=getResources();
//		Log.e("Position index", ""+position);
		Bitmap bitmap=channels.get(position).getProfile();
//
//		InputStream is = getResources().openRawResource(R.drawable.arrow_down);
//		Bitmap mBitmap = BitmapFactory.decodeStream(is);
		picView.setImageBitmap(bitmap);
//		fb.display(picView, uri);
	}



	/**
	 * to build the layout dynamically
	 * @param view
	 */
	/*****************************the layout algorithm********************************/
	int minimumY = 0;
	private void placeBrick(View view) {
		LayoutParams brick = (LayoutParams) view.getLayoutParams();
		int groupCount, colSpan, rowSpan;
		List<Integer> groupY = new ArrayList<Integer>();
		List<Integer> groupColY = new ArrayList<Integer>();
		colSpan = (int) Math.ceil(brick.width / this.columnWidth);
		colSpan = Math.min(colSpan, this.cols);
		rowSpan = (int) Math.ceil(brick.height / this.itemHeight);//column
		Log.d("VideoShowActivity", "colSpan:" + colSpan);
		if (colSpan == 1) {//no multiple column
			groupY = this.colYs;

			if (lostPoint.size() > 0 && rowSpan == 1) {
				Point point = lostPoint.get(0);
				int pTop = point.y;
				int pLeft = this.columnWidth * point.x;
				LayoutParams params = new LayoutParams(
						brick.width, brick.height);
				params.leftMargin = pLeft;
				params.topMargin = pTop;
				view.setLayoutParams(params);
				lostPoint.remove(0);
				return;
			}
		} else {// accupy multiple column
			groupCount = this.cols + 1 - colSpan;// index of column
			for (int j = 0; j < groupCount; j++) {
				groupColY = this.colYs.subList(j, j + colSpan);
				groupY.add(j, Collections.max(groupColY));// position to add view
			}
		}

		minimumY = Collections.min(groupY);// optimise the position
		int shortCol = 0;
		int len = groupY.size();
		for (int i = 0; i < len; i++) {
			if (groupY.get(i) == minimumY) {
				shortCol = i;// get the relevant column
				break;
			}
		}
		
		int pTop = minimumY;// put on top
		int pLeft = this.columnWidth * shortCol;// put on left
		LayoutParams params = new LayoutParams(
				brick.width, brick.height);
		params.leftMargin = pLeft;
		params.topMargin = pTop;
		view.setLayoutParams(params);
		if (colSpan != 1) {
			for (int i = 0; i < this.cols; i++) {
				if (minimumY > this.colYs.get(i)) {// space column
					int y = minimumY - this.colYs.get(i);
					for (int j = 0; j < y / itemHeight; j++) {
						lostPoint.add(new Point(i, this.colYs.get(i)
								+ itemHeight * j));
					}
				}
			}
		}
		int setHeight = minimumY + brick.height, setSpan = this.cols + 1 - len;
		for (int i = 0; i < setSpan; i++) {
			this.colYs.set(shortCol + i, setHeight);
		}
	}

	@SuppressLint("HandlerLeak")
	@SuppressWarnings("unchecked")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				loading_rl.setVisibility(View.GONE);
				postContent((List<String>)msg.obj);
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * dynamic layout ui
	 * @param result
	 */
	private void postContent(List<String> result) {
		// TODO Auto-generated method stub
		// calculate the  ListView
		if (result != null) {
			Random r = new Random();
			int[] layout = new int[result.size()];
			for(int i = 0; i < layout.length; i++)
				layout[i] = r.nextInt(50);
			for(int i = 0; i < layout.length; i++)
			{
				switch (layout[i])
				{
					case 1:
						break;
				}

				if (layout[i] > 40) {
					// 2 raw 2 column

				} else if (layout[i] > 25) {
					// 1 raw 2 column
					int remain = layout.length-i-1;
					if(remain==0)
						layout[i]=16;
					else if(remain ==1)
					{
						layout[i]=10;
						layout[i+1]=10;
						i = i+1;
					}
					else if(remain >=2)
					{
						layout[i+1]=10;
						layout[i+2]=10;
						i=i+2;
					}

				} else if (layout[i] > 15) {
					// 2 column 1 raw

				} else {//totally 2 raw
					int remain = layout.length-i-1;
					if(remain==0)
						layout[i]=16;
					else
					{
						layout[i+1]=10;
						i = i+1;
					}
				}
			}
			for (int i = 0; i < layout.length; i++) {
				View v = mInflater.inflate(R.layout.activity_cornerstone_item, null);
				int nextInt = layout[i];
				if (nextInt > 40) {
					// 2 raw 2 column
					LayoutParams params = new LayoutParams(columnWidth * 2, itemHeight * 2);
					v.setLayoutParams(params);
				} else if (nextInt > 25) {
					// 1 column 2 raw
					LayoutParams params = new LayoutParams(columnWidth, itemHeight * 2);
					v.setLayoutParams(params);
				} else if (nextInt > 15) {
					// 2 column 1 raw
					LayoutParams params = new LayoutParams(columnWidth * 2, itemHeight);
					v.setLayoutParams(params);
				} else {//totally 2 raw
					LayoutParams params = new LayoutParams(columnWidth, itemHeight);
					v.setLayoutParams(params);
				}
				addView(v, i);
//				addView(v, infos.size() + i);
			}
			infos.addAll(result);
		}
	}
	
	private void startAnim(View v) {
		final float centerX = columnWidth / 2.0f;
		final float centerY = itemHeight / 2.0f;
		Rotate3dAnimation rotation;
		rotation = new Rotate3dAnimation(10, 0, centerX, centerY);
		rotation.setDuration(1000);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new DecelerateInterpolator());
		v.startAnimation(rotation);
	}

}
