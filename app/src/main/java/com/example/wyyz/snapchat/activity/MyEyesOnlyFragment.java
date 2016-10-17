package com.example.wyyz.snapchat.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.Snap;
import com.example.wyyz.snapchat.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyEyesOnlyFragment extends Fragment implements View.OnClickListener{
    private GridView gridView;
    private LinearLayout pswLayout;
    private Button confirmBtn;
    private EditText passwordEt;
    private ImageAdapter lockedImgAdapter;
    private ArrayList<Snap> snaps=new ArrayList<Snap>();
    SnapChatDB db;

    public MyEyesOnlyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myEyeView= inflater.inflate(R.layout.fragment_myeyesonly, container, false);
        SnapActivity snapActivity = (SnapActivity) getActivity();
        gridView = (GridView) myEyeView.findViewById(R.id.gridView);
        pswLayout=(LinearLayout)myEyeView.findViewById(R.id.layout_password);
        confirmBtn=(Button)myEyeView.findViewById(R.id.btn_confirm);
        passwordEt =(EditText)myEyeView.findViewById(R.id.et_pwd);
        confirmBtn.setOnClickListener(this);
        db=SnapChatDB.getInstance(snapActivity);
        String email= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        User user=db.findUserByEmail(email);
        snaps=db.getUserLockedSnaps(user.getId());
        Log.d("snaps",String.valueOf(snaps.size()));
        lockedImgAdapter = new ImageAdapter(snapActivity, R.layout.image_item, snaps);
        gridView.setAdapter(lockedImgAdapter);
        return myEyeView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_confirm:
                String pwd=passwordEt.getText().toString();
                if(pwd.equals("123456")){
                    Log.d("correct password","correct");
                    pswLayout.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }
}
