package com.example.wyyz.snapchat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.activity.MyStory.StoryActivity;
import com.example.wyyz.snapchat.customView.CircleTextProgressbar;

import java.util.ArrayList;

/**
 * Created by Fallie on 17/10/2016.
 */

public class DisplaySnapActivity extends AppCompatActivity {
    private static final String TAG = "DisplaySnapActivity";
    private ArrayList<String> uris;
    private ImageView imageView;
    private CircleTextProgressbar mTvSkip;
    private int[] timers;
    // this is the roundth of the story
    private int round;
    private int tmp;
    private double upper;
    private double lower;
    private String activityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        uris = intent.getStringArrayListExtra("SnapPath");
        timers = intent.getIntArrayExtra("Timer");
        activityName = intent.getStringExtra("ActivityName");
        setContentView(R.layout.activity_display_snap);
        initialize();
        Log.i(TAG, "Activity created!");
    }

    private void initialize() {
        round = 0;
        imageView = (ImageView) findViewById(R.id.displayImage);
        mTvSkip = (CircleTextProgressbar) findViewById(R.id.countDown);
        mTvSkip.setCountdownProgressListener(1,progressListener);
        mTvSkip.setOutLineColor(Color.TRANSPARENT);
        mTvSkip.setInCircleColor(Color.parseColor("#AAC6C6C6"));
        mTvSkip.setProgressColor(Color.DKGRAY);
        mTvSkip.setProgressLineWidth(8);
        tmp = timers[0];
        upper = 100;
        lower = 100*(tmp-1)/timers[0];
        displaySnap(0);
        Log.i(TAG,"current tmp :" + tmp);
    }




    private CircleTextProgressbar.OnCountdownProgressListener progressListener = new CircleTextProgressbar.OnCountdownProgressListener() {

        @Override
        public void onProgress(int what, int progress) {
            if (what == 1) {

                if(progress == 0){
                    if(round+1 >= uris.size()){
                        finishDisplay();
                        Log.i(TAG,"finish display!!!");
                    }
                    else {
                        round ++;
                        tmp = timers[round];
                        upper = 100;
                        lower = 100*(tmp-1)/timers[round];
                        displaySnap(round);
                    }

                }

                if (progress > lower && progress < upper) {
                    mTvSkip.setText(String.valueOf(tmp));
                } else {
                    tmp--;
                    mTvSkip.setText(String.valueOf(tmp));
                    upper = 100 * tmp / timers[round];
                    lower = 100 * (tmp - 1) / timers[round];
                }

            }

        }
    };

    private void displaySnap(int i){
        Glide.with(getBaseContext())
                .load(uris.get(i))
                .into(imageView);
        mTvSkip.setTimeMillis(timers[i]*1000);
        mTvSkip.reStart();
        Log.i(TAG,"displaying snap!!");
    }

    private void finishDisplay() {
        Intent intent = new Intent();
        switch (activityName){
            case "StoryActivity":
                intent.setClass(DisplaySnapActivity.this, StoryActivity.class);
                break;
            case "SnapActivity":
                intent.setClass(DisplaySnapActivity.this, SnapActivity.class);
                break;
            case "ChatActivity":
                intent.setClass(DisplaySnapActivity.this, ChatActivity.class);
                break;
            default:
                break;
        }
        startActivity(intent);
        Log.i(TAG,"finish display!!!");
    }


}
