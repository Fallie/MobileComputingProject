package com.example.wyyz.snapchat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;


import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.customView.PageControlView;
import com.example.wyyz.snapchat.customView.ScrollLayout;
import com.example.wyyz.snapchat.db.DataBaseOperator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;



import static android.R.attr.data;

/**
 * GridView分页显示安装的应用程序
 */
public class DiscoverChannelActivity extends Activity {
	private ScrollLayout mScrollLayout;
	private static final float APP_PAGE_SIZE = 1.0f;
	private Context mContext;
	private PageControlView pageControl;
	private TextView subscribeText;
	private Button subscribeButton;
	private ProgressDialog dialog = null;
//	public MyHandler myHandler;
	public int n=0;
	private DataLoading dataLoad;
	private List<String> htmls;
	private int channelId;
	private boolean subscriptionState;
	private String channelName;
	private List<Spanned> discovers;

	private DataBaseOperator DBOperator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		setContentView(R.layout.discover_channel);
		dataLoad = new DataLoading();
		Intent intent = getIntent();

		htmls = intent.getStringArrayListExtra("contents");
		channelId = intent.getIntExtra("channelId",1);
		subscriptionState = intent.getBooleanExtra("subscription", false);
		channelName = intent.getStringExtra("channelName");
		mScrollLayout = (ScrollLayout)findViewById(R.id.ScrollLayoutTest);
		subscribeText = (TextView) findViewById(R.id.subscribeText);
		subscribeText.setText(channelName);

		subscribeButton = (Button) findViewById(R.id.subscribeButton);
		if(subscriptionState)
		{
			subscribeButton.setText("Unsubscribe");
			subscribeButton.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					Log.e("subscribe button","unsubscribe");
					unsubscribe();
					Message message = handler.obtainMessage();
					message.what = 3;
					handler.sendMessage(message);

				}
			});
		}
		else
		{
			subscribeButton.setText("Subscribe");
			subscribeButton.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					Log.e("subscribe button","subscribe");
					subscribe();
					Message message = handler.obtainMessage();
					message.what = 4;
					handler.sendMessage(message);

				}
			});
		}

		DBOperator = new DataBaseOperator(this);

		initComponent();

		discovers = new ArrayList<Spanned>();



		new Thread(new refreshThread()).start();
		dialog.show();

//		myHandler = new MyHandler(this,1);
		
//		//起一个线程更新数据
//		MyThread m = new MyThread();
//		new Thread(m).start();
	}
	
	/**
	 * gridView 的onItemLick响应事件
	 */
	public OnItemClickListener listener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			// TODO Auto-generated method stub
			System.out.println("position="+position);
		}
		
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}
//
//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		if(keyCode == KeyEvent.KEYCODE_BACK){
//			return true;
//		}else{
//			return super.onKeyUp(keyCode, event);
//		}
//	}
//
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//		finish();
//	}

	private void initComponent() {

		dialog = new ProgressDialog(this);
		dialog.setTitle("Notification");
		dialog.setMessage("Loading...");
		dialog.setCancelable(false);
	}


	
	
	
	// 更新后台数据
	class MyThread implements Runnable {
		public void run() {
//			try {
//				Thread.sleep(1000*3);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			Message message = Message.obtain();
			message.obj = data;
			message.what = 1;
			Log.e("send message","send message");
			handler.sendMessage(message);

		}
	}

	public class refreshThread implements Runnable {

		// 在run方法中完成网络耗时的操作
		@Override
		public void run() {
			try{

				final Html.ImageGetter imageGetter = new Html.ImageGetter() {

					public Drawable getDrawable(String source) {
						Drawable drawable=null;
						URL url;
						try {
							url = new URL(source);
							drawable = Drawable.createFromStream(url.openStream(), "");
						} catch (Exception e) {
							e.printStackTrace();
							return null;
						}
						Log.e("image download","download");
						int bwidth = drawable.getIntrinsicWidth();
						int bHeight = drawable.getIntrinsicHeight();

						int width = getWindowManager().getDefaultDisplay().getWidth();
						Log.e("====", bwidth + " " + bHeight + " " + width);
						int height = width * bHeight / bwidth;
						drawable.setBounds(0, 0, width, height);
						Log.e("image download","download finish");
						return drawable;
					}
				};

				Log.e("image download","download start");
				for(int i=0; i < htmls.size(); i++)
				{
					String html = htmls.get(i);
					Spanned sp = Html.fromHtml(html, imageGetter, null);
//					Spanned sp = null;
					discovers.add(sp);
				}
//				sp = Html.fromHtml(html, imageGetter, null);
				Log.e("image download","download return");

				// 这里的数据data我们必须发送给UI的主线程，所以我们通过Message的方式来做桥梁。
				Message message = Message.obtain();
				message.obj = data;
				message.what = 2;
				Log.e("send message","send message");
				handler.sendMessage(message);


			}
			catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private void subscribe()
	{
		DBOperator.subscribe(channelId);
	}

	private void unsubscribe()
	{
		DBOperator.unsubscribe(channelId);
	}

	private Handler handler = new Handler() {

		// 处理子线程给我们发送的消息。
		@Override
		public void handleMessage(Message msg) {
			Log.e("handler","receive message");
//			Log.e("handler","receive message");
//            byte[] data = (byte[])msg.obj;
			if(msg.what == 1){
				Log.e("multiple app","refresh components");
				int pageNo = (int) Math.ceil( discovers.size()/APP_PAGE_SIZE);
				for (int i = 0; i < pageNo; i++) {
					GridView appPage = new GridView(mContext);
					appPage.setSelector(new ColorDrawable(Color.TRANSPARENT));
					// get the "i" page data
					appPage.setAdapter(new AppAdapter(mContext, discovers, i));
					appPage.setNumColumns(1);
					appPage.setOnItemClickListener(listener);
					mScrollLayout.addView(appPage);
				}
				Log.e("multiple app","load pageControl");
				//加载分页
				pageControl = (PageControlView) findViewById(R.id.pageControl);
				pageControl.bindScrollViewGroup(mScrollLayout);
				Log.e("multiple app","load pageControl data");
				//加载分页数据
				dataLoad.bindScrollViewGroup(mScrollLayout);
			}
			else if(msg.what == 2)
			{
				Log.e("multiple app","download data finish");
				dialog.dismiss();
				//起一个线程更新数据
				MyThread m = new MyThread();
				new Thread(m).start();
			}
			else if(msg.what == 3)
			{
				subscribeButton.setText("Subscribe");
				subscribeButton.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						Log.e("subscribe button","subscribe");
						subscribe();
						Message message = handler.obtainMessage();
						message.what = 4;
						handler.sendMessage(message);

					}
				});
			}
			else if(msg.what == 4)
			{
				subscribeButton.setText("Unsubscribe");
				subscribeButton.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						Log.e("subscribe button","unsubscribe");
						unsubscribe();
						Message message = handler.obtainMessage();
						message.what = 3;
						handler.sendMessage(message);

					}
				});
			}
		};
	};

	
	
	//分页数据
	class DataLoading {
		private int count;
		public void bindScrollViewGroup(ScrollLayout scrollViewGroup) {
			this.count=scrollViewGroup.getChildCount();
			scrollViewGroup.setOnScreenChangeListenerDataLoad(new ScrollLayout.OnScreenChangeListenerDataLoad() {
				public void onScreenChange(int currentIndex) {
					// TODO Auto-generated method stub
					generatePageControl(currentIndex);
				}
			});
		}
		
		private void generatePageControl(int currentIndex){
//			//如果到最后一页，就加载16条记录
//			if(count==currentIndex+1){
//				MyThread m = new MyThread();
//				new Thread(m).start();
//			}
		}
	}
}
