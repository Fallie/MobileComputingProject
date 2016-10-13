package com.example.wyyz.snapchat.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.customView.TouchEventView;
import com.example.wyyz.snapchat.model.Snap;
import com.example.wyyz.snapchat.util.TmpPhotoView;
import com.example.wyyz.snapchat.util.TmpText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Fallie on 27/09/2016.
 */

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "PreviewActivity";
    private Snap photo;
    private Uri imageUri;
    private String imageUrl;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private static final int chosenSize = 0;
    private int secondNumber = 3;
    private boolean isTimerClicked = false;
    private Bitmap base;
    private RelativeLayout layout;
    private Button returnCamera;
    private Button nextStep;
    private Button saveToMemory;
    private Button timer;
    private Button addToStory;
    private Button addPicto;
    private Button addNote;
    private Button draw;
    private TouchEventView imageView;
    private NumberPicker np;
    private int chosenEmoticonId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        //photo.setUserId(mAuth.getInstance().getCurrentUser().getUid());
        //photo.setPhoto(tmpPhotoView.photo);

        initialize();




        Log.i(TAG, "Activity created!");
    }

    private void initialize() {
        photo = new Snap();
        base = TmpPhotoView.photo;
        layout = (RelativeLayout)findViewById(R.id.previewLayout);
        final TextView secondNum = (TextView) findViewById(R.id.secondNumber);
        imageView = (TouchEventView) findViewById(R.id.previewImage);
        imageView.setBackgroundDrawable(new BitmapDrawable(base));
        returnCamera = (Button) findViewById(R.id.btnReturnCamera);
        returnCamera.setOnClickListener(this);
        nextStep = (Button) findViewById(R.id.btnNextStep);
        nextStep.setOnClickListener(this);
        timer = (Button) findViewById(R.id.btnTimer);
        timer.setOnClickListener(this);
        addToStory = (Button) findViewById(R.id.btnAddToStory);
        addToStory.setOnClickListener(this);
        addPicto = (Button) findViewById(R.id.btnAddPicto);
        addPicto.setOnClickListener(this);
        addNote = (Button) findViewById(R.id.btnAddNote);
        addNote.setOnClickListener(this);
        saveToMemory = (Button) findViewById(R.id.btnSaveToMemory);
        saveToMemory.setOnClickListener(this);
        draw = (Button) findViewById(R.id.btnDraw);
        draw.setOnClickListener(this);

        np = (NumberPicker)findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(10);
        np.setWrapSelectorWheel(true);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub
                //picker.setValue((newVal < oldVal)?oldVal-5:oldVal+5);
                secondNumber = newVal;
                secondNum.setText(String.valueOf(newVal));

                Log.i(TAG,"now the second number is:   "+newVal);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReturnCamera:
                Intent intent = new Intent(PreviewActivity.this,CameraActivity.class);
                startActivity(intent);
                break;
            case R.id.btnNextStep:
                finish();
                //Intent intent = new Intent(PreviewActivity.this,CameraActivity.class);
                //startActivity(intent);
                finishSettingSnap();
                break;
            case R.id.btnTimer:
                setTimer();
                break;
            case R.id.btnAddToStory:
                addPhotoToStory();
                break;
            case R.id.btnAddPicto:
                beforeAddPicto();
                break;
            case R.id.btnAddNote:
                addText();
                break;
            case R.id.btnSaveToMemory:
                //savePhotoToMemory();
                downloadImagePublic();
                break;
            case R.id.btnDraw:
                startDraw();
                break;
            case R.id.id1:
                chosenEmoticonId = 1;
                break;
            case R.id.id2:
                chosenEmoticonId = 2;
                break;
            case R.id.id3:
                chosenEmoticonId = 3;
                break;
            case R.id.id4:
                chosenEmoticonId = 4;
                break;
            case R.id.id5:
                chosenEmoticonId = 5;
                break;
            case R.id.id6:
                chosenEmoticonId = 6;
                break;
            case R.id.id7:
                chosenEmoticonId = 7;
                break;
            case R.id.id8:
                chosenEmoticonId = 8;
                break;
            case R.id.id9:
                chosenEmoticonId = 9;
                break;
            default:
                break;

        }
    }

    protected void resumeCanvas() {

        Toast.makeText(PreviewActivity.this, "The canvas is cleared!",Toast.LENGTH_SHORT).show();
    }

    private Bitmap addWatermark(Bitmap src, Bitmap watermark) {
        if (src == null || watermark  == null) {
            Log.d(TAG, "src is null");
            return src;
        }

        int sWid = src.getWidth();
        int sHei = src.getHeight();
        int wWid = watermark.getWidth();
        int wHei = watermark.getHeight();
        if (sWid == 0 || sHei == 0) {
            return null;
        }

        if (sWid < wWid || sHei < wHei) {
            return src;
        }

        Bitmap bitmap = Bitmap.createBitmap(sWid, sHei, Bitmap.Config.ARGB_8888);
        try {
            Canvas cv = new Canvas(bitmap);
            cv.drawBitmap(src, 0, 0, null);
            cv.drawBitmap(watermark, wWid, wHei, null);
            cv.save(Canvas.ALL_SAVE_FLAG);
            cv.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }

    private void startDraw() {
        TmpPhotoView.photo = getBitmapFromView(imageView);
        layout.removeView(imageView);
        imageView.setBackgroundDrawable(new BitmapDrawable(TmpPhotoView.photo));
        layout.addView(imageView);
        imageView.setTypeCode(100);
        Log.i(TAG,"Start free hand drawing");
    }

    private void beforeAddPicto() {
        final Dialog builder0 = new Dialog(PreviewActivity.this);
        builder0.setTitle("Select Emoticon");
        builder0.setContentView(R.layout.dialogue_custom);
        builder0.show();
        Button okBtn = (Button) builder0.findViewById(R.id.dialogButtonOK);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPictogram();
                builder0.dismiss();
            }
        });
        Button cancelBtn = (Button) builder0.findViewById(R.id.dialogButtonCancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder0.cancel();
            }
        });
        ImageView emoticon1 = (ImageView) builder0.findViewById(R.id.id1);
        emoticon1.setOnClickListener(this);
        ImageView emoticon2 = (ImageView) builder0.findViewById(R.id.id2);
        emoticon2.setOnClickListener(this);
        ImageView emoticon3 = (ImageView) builder0.findViewById(R.id.id3);
        emoticon3.setOnClickListener(this);
        ImageView emoticon4 = (ImageView) builder0.findViewById(R.id.id4);
        emoticon4.setOnClickListener(this);
        ImageView emoticon5 = (ImageView) builder0.findViewById(R.id.id5);
        emoticon5.setOnClickListener(this);
        ImageView emoticon6 = (ImageView) builder0.findViewById(R.id.id6);
        emoticon6.setOnClickListener(this);
        ImageView emoticon7 = (ImageView) builder0.findViewById(R.id.id7);
        emoticon7.setOnClickListener(this);
        ImageView emoticon8 = (ImageView) builder0.findViewById(R.id.id8);
        emoticon8.setOnClickListener(this);
        ImageView emoticon9 = (ImageView) builder0.findViewById(R.id.id9);
        emoticon9.setOnClickListener(this);

    }

    private void addPictogram(){
        TmpPhotoView.photo = getBitmapFromView(imageView);
        layout.removeView(imageView);
        imageView.setBackgroundDrawable(new BitmapDrawable(TmpPhotoView.photo));
        layout.addView(imageView);
        imageView.setTypeCode(300);
        imageView.setFirstDraw(true);
        imageView.setBitmap(chosenEmoticonId);
        Log.i(TAG,"Start adding pictograms");
    }

    private void addText() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PreviewActivity.this);
        final EditText input = new EditText(this);
        builder1.setView(input);
        builder1.setTitle("Input Text");
        // Set up the buttons
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TmpText.textContent = input.getText().toString();
                TmpPhotoView.photo = getBitmapFromView(imageView);
                layout.removeView(imageView);
                imageView.setBackgroundDrawable(new BitmapDrawable(TmpPhotoView.photo));
                layout.addView(imageView);
                imageView.setTypeCode(200);
                Log.i(TAG,"Start adding text");
            }
        });
        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();

    }

    private void addPhotoToStory() {
        finishSettingSnap();
    }

    private void setTimer() {
        if(isTimerClicked)
        isTimerClicked = false;
        else isTimerClicked = true;
        setVisibility();
    }

    private void setVisibility(){
        if(isTimerClicked){
            np.setVisibility(View.VISIBLE);
        }
        else np.setVisibility(View.INVISIBLE);
    }

    private void finishSettingSnap(){
        //set the timer of the snap
        photo.setTimingOut(secondNumber);
        resetBase();
        photo.setPhoto(base);
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        photo.setTimestamp(timestamp);
        photo.setUserId(mAuth.getInstance().getCurrentUser().getUid());

    }

    public void downloadImagePublic() {
        finishSettingSnap();

        File dir = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!dir.exists()) {
            dir.mkdir();
        }


        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(dir.getPath() + File.separator + "IMG_" + timestamp + ".jpg");


        imageUri = Uri.fromFile(mediaFile);

        FileOutputStream outputStream = null;

        Bitmap newMap = getBitmapFromView(imageView);

        try {
            outputStream = new FileOutputStream(mediaFile);
            newMap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            //uploadImage(imageUri);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mediaFile)));
        //Toast.makeText(getApplicationContext(), "download successfully", Toast.LENGTH_LONG).show();
    }

    private void uploadImage(Uri imageUri) {
        // Get a reference to store file at photos/<FILENAME>.jpg
        StorageReference photoRef = mStorage.getInstance().getReference("Photos")
                .child(mAuth.getInstance().getCurrentUser().getUid())
                .child(imageUri.getLastPathSegment());
        // [END get_child_ref]

        // Upload file to Firebase Storage
        Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
        photoRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Upload succeeded
                        Log.d(TAG, "uploadFromUri:onSuccess");

                        // Get the public download URL
                        //Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();

                        // [START_EXCLUDE]


                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);

                        // [START_EXCLUDE]


                        // [END_EXCLUDE]
                    }
                });
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.TRANSPARENT);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void resetBase(){
        base = TmpPhotoView.photo;
    }


}

