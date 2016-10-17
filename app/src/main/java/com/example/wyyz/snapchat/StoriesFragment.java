package com.example.wyyz.snapchat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.wyyz.snapchat.activity.ImageAdapter;
import com.example.wyyz.snapchat.activity.SnapActivity;
import com.example.wyyz.snapchat.model.Snap;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoriesFragment extends Fragment {
    private GridView gridView;
    private ImageAdapter storyImgAdapter;
    private ArrayList<Snap> snaps=new ArrayList<Snap>();

    public StoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View storyView=inflater.inflate(R.layout.fragment_stories, container, false);
        SnapActivity snapActivity = (SnapActivity) getActivity();
        gridView = (GridView) storyView.findViewById(R.id.gridView);
        storyImgAdapter = new ImageAdapter(snapActivity, R.layout.image_item, snaps);
        gridView.setAdapter(storyImgAdapter);
        return storyView;
    }

}
