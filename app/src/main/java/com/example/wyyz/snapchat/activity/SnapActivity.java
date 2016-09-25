package com.example.wyyz.snapchat.activity;

/**
 * Created by Fallie on 21/09/2016.
 */


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class SnapActivity extends FragmentActivity implements View.OnClickListener {

    private SnapChatDB snapChatDB;

    private String mUsername = "";
    private String mPhotoUrl = "";

    private ViewPager pager ;
    private ArrayList<Fragment> fragments;
    private PagerAdapter adapter;
    private TextView tv_tab0, tv_tab1, tv_tab2,tv_tab3;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        snapChatDB = SnapChatDB.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();
        feedData();
        initView();
    }

    private void initView(){
        fragments=new ArrayList<Fragment>();
        fragments.add(new ChatlistFragment());
        fragments.add(new CameraFragment());
        fragments.add(new DiscoverFragment());
        adapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        pager=(ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tv_tab0=(TextView) findViewById(R.id.tv_tab0);
        tv_tab1=(TextView) findViewById(R.id.tv_tab1);
        tv_tab2=(TextView) findViewById(R.id.tv_tab2);
        tv_tab3=(TextView) findViewById(R.id.tv_tab3);

        pager.setCurrentItem(1);
        tv_tab0.setTextColor(Color.BLACK);
        tv_tab1.setTextColor(Color.RED);
        tv_tab2.setTextColor(Color.BLACK);
        tv_tab3.setTextColor(Color.BLACK);
        tv_tab0.setOnClickListener(this);
        tv_tab1.setOnClickListener(this);
        tv_tab2.setOnClickListener(this);
        tv_tab3.setOnClickListener(this);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            public void onPageSelected(int position){
                switch (position){
                    case 0:
                        tv_tab0.setTextColor(Color.RED);
                        tv_tab1.setTextColor(Color.BLACK);
                        tv_tab2.setTextColor(Color.BLACK);
                        tv_tab3.setTextColor(Color.BLACK);
                        break;
                    case 1:
                        tv_tab0.setTextColor(Color.BLACK);
                        tv_tab1.setTextColor(Color.RED);
                        tv_tab2.setTextColor(Color.BLACK);
                        tv_tab3.setTextColor(Color.BLACK);
                        break;
                    case 2:
                        tv_tab0.setTextColor(Color.BLACK);
                        tv_tab1.setTextColor(Color.BLACK);
                        tv_tab2.setTextColor(Color.RED);
                        tv_tab3.setTextColor(Color.BLACK);
                        break;
                    case 3:
                        tv_tab0.setTextColor(Color.BLACK);
                        tv_tab1.setTextColor(Color.BLACK);
                        tv_tab2.setTextColor(Color.BLACK);
                        tv_tab3.setTextColor(Color.RED);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_tab0:
                pager.setCurrentItem(0);
                break;
            case R.id.tv_tab1:
                pager.setCurrentItem(1);
                break;
            case R.id.tv_tab2:
                pager.setCurrentItem(2);
                break;
            case R.id.tv_tab3:
                firebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public SnapChatDB getDbInstance(){
        return snapChatDB;
    }

    private void feedData(){
        User user=snapChatDB.findUserByUsername("ziyuan_w");
        if(user==null){
            snapChatDB.seedData();
        }
    }

}