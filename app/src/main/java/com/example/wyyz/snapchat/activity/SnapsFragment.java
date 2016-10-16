package com.example.wyyz.snapchat.activity;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.Snap;
import com.example.wyyz.snapchat.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SnapsFragment extends Fragment {
    private GridView gridView;
    private ImageAdapter snapImgAdapter;
    private ArrayList<Snap> snaps=new ArrayList<Snap>();
    SnapChatDB db;
    StorageReference storageRef;

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
        db=SnapChatDB.getInstance(snapActivity);
        String email=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        User user=db.findUserByEmail(email);
        snaps=db.getUserSnap(user.getId());
        Log.d("snaps",String.valueOf(snaps.size()));
        for(final Snap s:snaps){
            String url=s.getPath();
            //Uri photoUri = Uri.parse(url);
            Log.d("url",String.valueOf(s.getUserId()));
            Log.d("url",s.getTimestamp());
            Log.d("url",url);
            Log.d("url",String.valueOf(s.getSize()));
            storageRef= FirebaseStorage.getInstance().getReferenceFromUrl(url);
            final long ONE_MEGABYTE = s.getSize();
            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    s.setPhoto(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    snapImgAdapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d("Failed","Failed");
                }
            });
        }
        Log.d("snaps",String.valueOf(snaps.size()));
        snapImgAdapter = new ImageAdapter(snapActivity, R.layout.image_item, snaps);
        gridView.setAdapter(snapImgAdapter);
        return snapsView;
    }

    public ImageAdapter getSnapImgAdapter() {
        return snapImgAdapter;
    }
    public Snap getSnap(int position){
        return snaps.get(position);
    }
}
