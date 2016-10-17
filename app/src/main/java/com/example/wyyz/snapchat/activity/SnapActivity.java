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
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SnapActivity extends FragmentActivity implements View.OnClickListener {
    CameraRollFragment cameraRollFragment;
    SnapsFragment snapsFragment;
    private ArrayList<Integer> selectedSnapsId = new ArrayList<>();
    private boolean[] selectMap;

    private ViewPager pager ;
    private ArrayList<Fragment> fragments;
    private PagerAdapter adapter;
    private TextView tv_tab0, tv_tab1, tv_tab2, tv_tab3;
    private FirebaseAuth firebaseAuth;
    Button btnCamera;

    private RelativeLayout titleLayout;
    private RelativeLayout selectTopLayout;
    private RelativeLayout selectBottomLayout;
    private ImageView ivSelect;
    private ImageView ivCancel;
    private ImageView ivSend;
    private ImageView ivDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap);
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
        ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click","1");
                titleLayout.setVisibility(View.INVISIBLE);
                selectTopLayout.setVisibility(View.VISIBLE);
                selectBottomLayout.setVisibility(View.VISIBLE);
                btnCamera.setVisibility(View.GONE);
                cameraRollFragment.getCameraRollImgAdapter().toggleOnSelect();
                snapsFragment.getSnapImgAdapter().toggleOnSelect();
            }
        });
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleLayout.setVisibility(View.VISIBLE);
                selectTopLayout.setVisibility(View.GONE);
                selectBottomLayout.setVisibility(View.GONE);
                btnCamera.setVisibility(View.VISIBLE);
                cameraRollFragment.getCameraRollImgAdapter().toggleOffSelect();
                snapsFragment.getSnapImgAdapter().toggleOffSelect();
            }
        });
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               selectMap=snapsFragment.getSnapImgAdapter().getSelectMap();
                for(int i=0;i<selectMap.length;i++){
                    if(selectMap[i]){
                        selectedSnapsId.add(snapsFragment.getSnap(i).getId());
                    }
                }

                Log.d("selected",String.valueOf(selectedSnapsId.size()));
                titleLayout.setVisibility(View.VISIBLE);
                selectTopLayout.setVisibility(View.GONE);
                selectBottomLayout.setVisibility(View.GONE);
                btnCamera.setVisibility(View.VISIBLE);
                cameraRollFragment.getCameraRollImgAdapter().toggleOffSelect();
                snapsFragment.getSnapImgAdapter().toggleOffSelect();
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
        selectBottomLayout=(RelativeLayout)findViewById(R.id.select_bottom);
        ivSelect=(ImageView)findViewById(R.id.imgv_select);
        ivCancel=(ImageView)findViewById(R.id.imgv_cancel);
        ivSend=(ImageView)findViewById(R.id.imgv_send);
        ivDelete=(ImageView)findViewById(R.id.imgv_delete);

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
