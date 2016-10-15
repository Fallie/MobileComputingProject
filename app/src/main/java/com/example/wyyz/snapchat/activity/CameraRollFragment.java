package com.example.wyyz.snapchat.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
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
 * A simple {@link Fragment} subclass.
 */
public class CameraRollFragment extends Fragment{
    private GridView gridView;
    private ImageAdapter cameraRollImgAdapter;
    private ArrayList<Snap> snaps=new ArrayList<Snap>();
    private SnapActivity snapActivity;

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
        cameraRollImgAdapter = new ImageAdapter(snapActivity, R.layout.image_item, snaps);
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
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, h, w, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
    public static Bitmap getLocalBitmap(String path){
        Bitmap bitmap=BitmapFactory.decodeFile(path);
        return bitmap;
    }

    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength)
    {
        if(null == bitmap || edgeLength <= 0)
        {
            return  null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if(widthOrg > edgeLength && heightOrg > edgeLength)
        {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;

            try{
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            }
            catch(Exception e){
                return null;
            }

            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;

            try{
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            }
            catch(Exception e){
                return null;
            }
        }

        return result;
    }

    private void getSnapsFromSystemPhotos(Context context){
        //ArrayList<Snap> snaps=new ArrayList<Snap>();
        List<String> paths=getSystemPhotoList(context);
        for(int i=paths.size()-1;i>=0;i--){
            Bitmap photo=getLocalBitmap(paths.get(i));
            Bitmap squarephoto=centerSquareScaleBitmap(photo,100);
            Snap snap=new Snap();
            snap.setPhoto(squarephoto);
            snap.setPath(paths.get(i));
            snaps.add(snap);
        }
    }

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
