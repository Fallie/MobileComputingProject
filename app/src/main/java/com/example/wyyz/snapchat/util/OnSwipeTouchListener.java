package com.example.wyyz.snapchat.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.activity.CameraActivity;
import com.example.wyyz.snapchat.activity.DiscoverActivity;
import com.example.wyyz.snapchat.activity.MyStory.StoryActivity;
import com.example.wyyz.snapchat.activity.MyfriendsActivity;
import com.example.wyyz.snapchat.activity.ProfileActivity;
import com.example.wyyz.snapchat.activity.SnapActivity;

/**
 * Created by Fallie on 14/10/2016.
 */

public class OnSwipeTouchListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;
    private Activity activity;
    private String tag;

    public OnSwipeTouchListener(Context ctx,CameraActivity activity) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        this.activity = activity;
        this.tag = activity.TAG;
    }

    public OnSwipeTouchListener(Context ctx,MyfriendsActivity activity) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        this.activity = activity;
        this.tag = activity.TAG;
    }

    public OnSwipeTouchListener(Context ctx,DiscoverActivity activity) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        this.activity = activity;
        this.tag = activity.TAG;
    }

    public OnSwipeTouchListener(Context ctx,StoryActivity activity) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        this.activity = activity;
        this.tag = activity.TAG;
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
        Intent intent = new Intent();
        if(tag == "CameraActivity"){
            intent.setClass(this.activity, MyfriendsActivity.class);
        }
        else if(tag == "StoryActivity")
        {
            intent.setClass(this.activity, CameraActivity.class);
        }
        if(tag == "DiscoverActivity"){
            intent.setClass(this.activity, StoryActivity.class);
        }
        this.activity.startActivity(intent);
        this.activity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

    }

    public void onSwipeLeft() {
        Intent intent = new Intent();
        if(tag == "CameraActivity"){
            intent.setClass(this.activity, StoryActivity.class);
        }
        else if(tag == "StoryActivity")
        {
            intent.setClass(this.activity, DiscoverActivity.class);
        }
        if(tag == "MyfriendsActivity"){
            intent.setClass(this.activity, CameraActivity.class);
        }
        this.activity.startActivity(intent);
        this.activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

    }

    public void onSwipeTop() {
        Intent intent = new Intent();
        intent.setClass(this.activity, SnapActivity.class);
        this.activity.startActivity(intent);
        this.activity.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    public void onSwipeBottom() {
        Intent intent = new Intent();
        intent.setClass(this.activity, ProfileActivity.class);
        this.activity.startActivity(intent);
        this.activity.overridePendingTransition(R.anim.in_from_top, R.anim.out_to_bottom);
    }

}
