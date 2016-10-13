package com.example.wyyz.snapchat.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.wyyz.snapchat.R;

import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends BaseAdapter {
	private List<Spanned> mList;
	private Context mContext;
	public static final int APP_PAGE_SIZE = 1;
	private PackageManager pm;
	
	public AppAdapter(Context context, List<Spanned> list, int page) {
		mContext = context;
		pm = context.getPackageManager();
		
		mList = new ArrayList<Spanned>();
		int i = page * APP_PAGE_SIZE;
		int iEnd = i+APP_PAGE_SIZE;
		while ((i<list.size()) && (i<iEnd)) {
			mList.add(list.get(i));
			i++;
		}
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Spanned appInfo = mList.get(position);
		AppItem appItem;
		if (convertView == null) {
			View v = LayoutInflater.from(mContext).inflate(R.layout.discover_content, null);
			
			appItem = new AppItem();
			appItem.mContent = (TextView)v.findViewById(R.id.textView1);
			
			v.setTag(appItem);
			convertView = v;
		} else {
			appItem = (AppItem)convertView.getTag();
		}
		// set the icon
//		appItem.mAppIcon.setImageResource(R.drawable.icon);
		// set the app name
//		Log.e("item transfer","transfer");
		appItem.mContent.setText(appInfo);
		
		return convertView;
	}

	/**
	 * 每个应用显示的内容，包括图标和名称
	 * @author Yao.GUET
	 *
	 */
	class AppItem {
		TextView mContent;
	}
}
