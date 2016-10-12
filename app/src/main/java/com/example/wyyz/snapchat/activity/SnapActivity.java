package com.example.wyyz.snapchat.activity;

/**
 * Created by Fallie on 21/09/2016.
 */


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class SnapActivity extends FragmentActivity implements View.OnClickListener {

    private SnapChatDB snapChatDB;

    private String mUsername = "";
    private String mPhotoUrl = "";

    private ViewPager pager ;
    private ArrayList<Fragment> fragments;
    private PagerAdapter adapter;
    private TextView tv_tab0, tv_tab1, tv_tab2;
    private FirebaseAuth firebaseAuth;
    Button btnCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap);
        snapChatDB = SnapChatDB.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();
        btnCamera = (Button) findViewById(R.id.btnButton);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SnapActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
        initView();
    }

    private void initView(){
        fragments=new ArrayList<Fragment>();
        fragments.add(new SnapsFragment());
        fragments.add(new CameraRollFragment());
        fragments.add(new DiscoverFragment());
        adapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        pager=(ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tv_tab0=(TextView) findViewById(R.id.tv_tab0);
        tv_tab1=(TextView) findViewById(R.id.tv_tab1);
        tv_tab2=(TextView) findViewById(R.id.tv_tab2);

        pager.setCurrentItem(1);
        tv_tab0.setTextColor(getResources().getColor(R.color.previewBackground));
        tv_tab1.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_tab2.setTextColor(getResources().getColor(R.color.previewBackground));
        tv_tab0.setOnClickListener(this);
        tv_tab1.setOnClickListener(this);
        tv_tab2.setOnClickListener(this);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            public void onPageSelected(int position){
                switch (position){
                    case 0:
                        tv_tab0.setTextColor(getResources().getColor(R.color.colorAccent));
                        tv_tab1.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab2.setTextColor(getResources().getColor(R.color.previewBackground));
                        break;
                    case 1:
                        tv_tab0.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab1.setTextColor(getResources().getColor(R.color.colorAccent));
                        tv_tab2.setTextColor(getResources().getColor(R.color.previewBackground));
                        break;
                    case 2:
                        tv_tab0.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab1.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab2.setTextColor(getResources().getColor(R.color.colorAccent));
                        break;
                    case 3:
                        tv_tab0.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab1.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab2.setTextColor(getResources().getColor(R.color.previewBackground));
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
            default:
                break;
        }
    }

    public SnapChatDB getDbInstance(){
        return snapChatDB;
    }

    public void signOut(){
        firebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
