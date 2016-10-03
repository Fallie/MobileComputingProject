package com.example.wyyz.snapchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wyyz.snapchat.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {
    TextView profile;


    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View cameraView= inflater.inflate(R.layout.fragment_camera, container, false);
        final SnapActivity snapActivity = (SnapActivity) getActivity();
        profile=(TextView)cameraView.findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent=new Intent(snapActivity, ProfileActivity.class);
                startActivity(intent);
            }
        });
        return cameraView;
    }

}
