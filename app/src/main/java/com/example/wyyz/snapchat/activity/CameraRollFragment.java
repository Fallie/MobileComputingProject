package com.example.wyyz.snapchat.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.Snap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraRollFragment extends Fragment {
    private GridView gridView;
    private ImageAdapter imageAdapter;
    private ArrayList<Snap> snaps=new ArrayList<Snap>();

    public CameraRollFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View camerarollView= inflater.inflate(R.layout.fragment_snaps, container, false);
        final SnapActivity snapActivity = (SnapActivity) getActivity();
        gridView = (GridView) camerarollView.findViewById(R.id.gridView);
        snaps=getSnapsFromSystemPhotos(snapActivity);
        imageAdapter = new ImageAdapter(snapActivity, R.layout.image_item, snaps);
        gridView.setAdapter(imageAdapter);

        return camerarollView;
    }

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

    private Bitmap getBitmapFromImagePath(String path){
        Bitmap bitmap = null;
        //get height and width of image
        BitmapFactory.Options options = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        int h = options.outHeight;
        int w = options.outWidth;
        //Read the scaled bitmap
        bitmap = BitmapFactory.decodeFile(path, options);
        // Use ThumbnailUtils to create thumbnails, here to specify the bitmap to zoom objects
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    private ArrayList<Snap> getSnapsFromSystemPhotos(Context context){
        ArrayList<Snap> snaps=new ArrayList<Snap>();
        List<String> paths=getSystemPhotoList(context);
        for(int i=0;i<paths.size();i++){
            Bitmap photo=getBitmapFromImagePath(paths.get(i));
            Snap snap=new Snap();
            snap.setPhoto(photo);
            snaps.add(snap);
        }
        return snaps;
    }
}
