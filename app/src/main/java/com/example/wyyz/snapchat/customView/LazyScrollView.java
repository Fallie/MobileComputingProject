package com.example.wyyz.snapchat.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class LazyScrollView extends ScrollView {

	private View view;

	public LazyScrollView(Context context) {
		super(context);

	}

	public LazyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public LazyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (onScrollListener != null)
			onScrollListener.onAutoScroll(l, t, oldl, oldt);
	}

	private void init() {
		this.setOnTouchListener(onTouchListener);
	}

	OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if (view.getMeasuredHeight() - 20 <= getScrollY() + getHeight()) {
					if (onScrollListener != null) {
						onScrollListener.onBottom();
					}
				}
				break;

			default:
				break;
			}
			return false;
		}

	};

	/**
	 *
	 */
	public void getView() {
		this.view = getChildAt(0);
		if (view != null) {
			init();
		}
	}

	/**
	 * define the  interface
	 * 
	 * @author admin
	 * 
	 */
	public interface OnScrollListener {
		public void onBottom();
		public void onAutoScroll(int l, int t, int oldl, int oldt);
	}

	private OnScrollListener onScrollListener;

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}
}
