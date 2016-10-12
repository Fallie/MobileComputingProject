package com.example.wyyz.snapchat.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.Snap;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SnapsFragment extends Fragment {
    private GridView gridView;
    private ImageAdapter imageAdapter;
    private ArrayList<Snap> snaps=new ArrayList<Snap>();

    public SnapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View snapsView=inflater.inflate(R.layout.fragment_snaps, container, false);
        final SnapActivity snapActivity = (SnapActivity) getActivity();
        gridView = (GridView) snapsView.findViewById(R.id.gridView);
        imageAdapter = new ImageAdapter(snapActivity, R.layout.image_item, snaps);
        gridView.setAdapter(imageAdapter);
        return snapsView;
    }

}
