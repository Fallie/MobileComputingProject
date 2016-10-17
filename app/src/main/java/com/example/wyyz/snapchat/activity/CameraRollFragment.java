package com.example.wyyz.snapchat.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.Snap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Fragment to display pictures from local camera roll
 */
public class CameraRollFragment extends Fragment{
    private GridView gridView;
    private ImageAdapter cameraRollImgAdapter;
    private ArrayList<Snap> snaps=new ArrayList<Snap>();
    private SnapActivity snapActivity;
    Bitmap bitmap;
    Bitmap squarephoto;

    public CameraRollFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View camerarollView= inflater.inflate(R.layout.fragment_snaps, container, false);
        snapActivity = (SnapActivity) getActivity();
        gridView = (GridView) camerarollView.findViewById(R.id.gridView);
        cameraRollImgAdapter = new ImageAdapter(snapActivity, R.layout.image_item, snaps,"My CameraRoll");
        gridView.setAdapter(cameraRollImgAdapter);
        new GetSnapsFromSystem().execute();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(snapActivity, "Should Edit Photo!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return camerarollView;
    }

    //get a list of picture path of all the local pictures
    private List<String> getSystemPhotoList(Context context){
        List<String> result = new ArrayList<String>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) return null; // No Picture
        while (cursor.moveToNext())
        {
            int index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(index); // path
            File file = new File(path);
            if (file.exists())
            {
                result.add(path);
                Log.i("Path", path);
            }
        }

        return result ;
    }

    //Create snaps for local pictures
    private void getSnapsFromSystemPhotos(Context context){
        //ArrayList<Snap> snaps=new ArrayList<Snap>();
        List<String> paths=getSystemPhotoList(context);
        if(paths.size()>0) {
            for (int i = paths.size() - 1; i >= 0; i--) {
                Snap snap = new Snap();
                snap.setPath(paths.get(i));
                snaps.add(snap);
            }
        }
    }

    //AsyncTask to display local pictures
    class GetSnapsFromSystem extends AsyncTask<Void, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            getSnapsFromSystemPhotos(snapActivity);
            return true;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            cameraRollImgAdapter.notifyDataSetChanged();
        }
    }
    public ImageAdapter getCameraRollImgAdapter() {
        return cameraRollImgAdapter;
    }

    public ArrayList<Snap> getSnaps() {
        return snaps;
    }
}
