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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.Snap;
import com.example.wyyz.snapchat.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SnapActivity extends FragmentActivity implements View.OnClickListener {
    CameraRollFragment cameraRollFragment;
    SnapsFragment snapsFragment;
    private ArrayList<Snap> selectedSnaps = new ArrayList<>();
    private boolean[] selectMap;
    private User user;
    SnapChatDB db;

    private ViewPager pager ;
    private ArrayList<Fragment> fragments;
    private PagerAdapter adapter;
    private TextView tv_tab0, tv_tab1, tv_tab2, tv_tab3;
    private FirebaseAuth firebaseAuth;
    Button btnCamera;
    private Button btnCreateStory;

    private RelativeLayout titleLayout;
    private RelativeLayout selectTopLayout;
    private ImageView ivSelect;
    private ImageView ivCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap);
        db=SnapChatDB.getInstance(SnapActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();
        String email=firebaseAuth.getInstance().getCurrentUser().getEmail();
        final User user=db.findUserByEmail(email);
        btnCamera = (Button) findViewById(R.id.btnButton);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SnapActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
        initView();
        ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click","1");
                titleLayout.setVisibility(View.INVISIBLE);
                selectTopLayout.setVisibility(View.VISIBLE);
                btnCreateStory.setVisibility(View.VISIBLE);
                btnCamera.setVisibility(View.GONE);
                cameraRollFragment.getCameraRollImgAdapter().disableSelectImg();
                snapsFragment.getSnapImgAdapter().toggleOnSelect();
            }
        });
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleLayout.setVisibility(View.VISIBLE);
                selectTopLayout.setVisibility(View.GONE);
                btnCreateStory.setVisibility(View.GONE);
                btnCamera.setVisibility(View.VISIBLE);
                cameraRollFragment.getCameraRollImgAdapter().toggleOffSelect();
                snapsFragment.getSnapImgAdapter().toggleOffSelect();
            }
        });
        btnCreateStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               selectMap=snapsFragment.getSnapImgAdapter().getSelectMap();
                for(int i=0;i<selectMap.length;i++){
                    if(selectMap[i]){
                        Log.d("selected",String.valueOf(snapsFragment.getSnap(i).getId()));
                        selectedSnaps.add(snapsFragment.getSnap(i));
                    }
                }
                Log.d("selected",String.valueOf(selectedSnaps.size()));
                db.saveStory(user,selectedSnaps);
                titleLayout.setVisibility(View.VISIBLE);
                selectTopLayout.setVisibility(View.GONE);
                btnCreateStory.setVisibility(View.GONE);
                btnCamera.setVisibility(View.VISIBLE);
                cameraRollFragment.getCameraRollImgAdapter().toggleOffSelect();
                snapsFragment.getSnapImgAdapter().toggleOffSelect();

                ArrayList<String> paths = new ArrayList<String>();
                ArrayList<Integer> timers=new ArrayList<Integer>();
                for(int i=0;i<selectedSnaps.size();i++){
                    paths.add(selectedSnaps.get(i).getPath());
                    timers.add(selectedSnaps.get(i).getTimingOut());
                    Log.d("path",String.valueOf(selectedSnaps.get(i).getPath()));
                    Log.d("path",String.valueOf(selectedSnaps.get(i).getTimingOut()));
                }
                Intent intent = new Intent(SnapActivity.this, DisplaySnapActivity.class);
                intent.putExtra("ActivityName","SnapActivity");
                intent.putExtra("SnapPath",paths);
                intent.putExtra("Timer",timers);
                startActivity(intent);
            }
        });
    }

    protected void onStart(){
        super.onStart();

        for (Fragment f:fragments) {
            if(f instanceof CameraRollFragment){
                cameraRollFragment=(CameraRollFragment) f;
            }else if(f instanceof SnapsFragment){
                snapsFragment=(SnapsFragment)f;
            }
        }
    }

    private void initView(){
        titleLayout=(RelativeLayout)findViewById(R.id.title_layout);
        selectTopLayout=(RelativeLayout)findViewById(R.id.select_top);
        btnCreateStory=(Button)findViewById(R.id.btnCreate);
        ivSelect=(ImageView)findViewById(R.id.imgv_select);
        ivCancel=(ImageView)findViewById(R.id.imgv_cancel);

        fragments=new ArrayList<Fragment>();
        fragments.add(new SnapsFragment());
        fragments.add((new StoriesFragment()));
        fragments.add(new CameraRollFragment());
        fragments.add(new MyEyesOnlyFragment());
        adapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        pager=(ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tv_tab0=(TextView) findViewById(R.id.tv_tab0);
        tv_tab1=(TextView) findViewById(R.id.tv_tab1);
        tv_tab2=(TextView) findViewById(R.id.tv_tab2);
        tv_tab3=(TextView) findViewById(R.id.tv_tab3);

        pager.setCurrentItem(0);
        tv_tab0.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_tab1.setTextColor(getResources().getColor(R.color.previewBackground));
        tv_tab2.setTextColor(getResources().getColor(R.color.previewBackground));
        tv_tab3.setTextColor(getResources().getColor(R.color.previewBackground));
        tv_tab0.setOnClickListener(this);
        tv_tab1.setOnClickListener(this);
        tv_tab2.setOnClickListener(this);
        tv_tab3.setOnClickListener(this);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            public void onPageSelected(int position){
                switch (position){
                    case 0:
                        tv_tab0.setTextColor(getResources().getColor(R.color.colorAccent));
                        tv_tab1.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab2.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab3.setTextColor(getResources().getColor(R.color.previewBackground));
                        break;
                    case 1:
                        tv_tab0.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab1.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab2.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab3.setTextColor(getResources().getColor(R.color.colorAccent));
                        break;
                    case 2:
                        tv_tab0.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab1.setTextColor(getResources().getColor(R.color.colorAccent));
                        tv_tab2.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab3.setTextColor(getResources().getColor(R.color.previewBackground));
                        break;
                    case 3:
                        tv_tab0.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab1.setTextColor(getResources().getColor(R.color.previewBackground));
                        tv_tab2.setTextColor(getResources().getColor(R.color.colorAccent));
                        tv_tab3.setTextColor(getResources().getColor(R.color.previewBackground));
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
                pager.setCurrentItem(2);
                break;
            case R.id.tv_tab2:
                pager.setCurrentItem(3);
                break;
            case R.id.tv_tab3:
                pager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

}
