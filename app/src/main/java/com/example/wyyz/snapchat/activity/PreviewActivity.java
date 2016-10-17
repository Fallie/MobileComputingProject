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
import com.example.wyyz.snapchat.activity.MyStory.StoryActivity;
import com.example.wyyz.snapchat.customView.TouchEventView;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.MyStorySnap;
import com.example.wyyz.snapchat.model.Snap;
import com.example.wyyz.snapchat.model.User;
import com.example.wyyz.snapchat.util.ConnectionDetector;
import com.example.wyyz.snapchat.util.ShowNetworkAlert;
import com.example.wyyz.snapchat.util.TmpPhotoView;
import com.example.wyyz.snapchat.util.TmpText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fallie on 27/09/2016.
 */

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "PreviewActivity";
    private Snap photo;
    private SnapChatDB snapChatDB;
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
    private Button cancelDrawing;
    private Button draw;
    private TouchEventView imageView;
    private NumberPicker np;
    private int chosenEmoticonId;
    private SnapChatDB db;
    private Intent intent;
    private String uri;
    private String timeStamp;
    // Connection detector class
    private ConnectionDetector cd;
    // flag for Internet connection status
    private Boolean isInternetPresent = false;
    // Alert Dialog Manager
    private ShowNetworkAlert alert = new ShowNetworkAlert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        intent = getIntent();
        uri = intent.getStringExtra("SnapPath");
        timeStamp = intent.getStringExtra("TimeStamp");
        initialize();
        db=SnapChatDB.getInstance(PreviewActivity.this);

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
        cancelDrawing = (Button) findViewById(R.id.btnCancel);
        cancelDrawing.setOnClickListener(this);
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
                Intent intentNext = new Intent(PreviewActivity.this, MyfriendsActivity.class);
                intentNext.putExtra("hasImage",true);
                startActivity(intentNext);
                finish();
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
                popUpSaveSnap();
                break;
            case R.id.btnDraw:
                startDraw();
                break;
            case R.id.btnCancel:
                cancelDrawing();
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

    private void popUpSaveSnap() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PreviewActivity.this);
        builder1.setTitle("Where do you wanna save to?");
        // Set up the buttons
        builder1.setPositiveButton("Memory", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cd = new ConnectionDetector(getApplicationContext());
                checkavailability();
                finishSettingSnap();
                Log.i(TAG,"Start deleting text");
            }
        });
        builder1.setNegativeButton("Local Gallary", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                downloadImagePublic();
            }
        });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
    }
    public void checkavailability() {
        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(PreviewActivity.this,
                    "Fail",
                    "Internet Connection is NOT Available", false);
            // stop executing code by return
            return;
        }
    }



    protected void resumeCanvas() {

        Toast.makeText(PreviewActivity.this, "The canvas is cleared!",Toast.LENGTH_SHORT).show();
    }

    private void cancelDrawing() {
        //reset the background
        TmpPhotoView.photo = TmpPhotoView.copy;
        Intent intent = getIntent();
        startActivity(intent);
        Log.i(TAG,"HERE CANCELS THE DRAWING");
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
        TmpPhotoView.copy = null;

        String userId = mAuth.getInstance().getCurrentUser().getUid();
        StorageReference photoRef = mStorage.getInstance().getReference("MyStory").child(userId);
        StorageReference mountainsRef = photoRef.child(photo.getTimestamp()+".jpg");


        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        byte[] data = os.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.e("image save url",downloadUrl.toString());


                String userId = mAuth.getInstance().getCurrentUser().getUid();
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference();

                DatabaseReference myStoryRef = ref.child("MyStory").child(userId).child(photo.getTimestamp());



                Map<String, Object> updates = new HashMap<String, Object>();
                updates.put("timeStamp", photo.getTimestamp());
                updates.put("timingout", photo.getTimingOut());

                updates.put("url", downloadUrl.toString());
                myStoryRef.updateChildren(updates);

                Log.e("image remote save","finished");

                MyStorySnap myStorySnap = new MyStorySnap();
                myStorySnap.setPath(downloadUrl.toString());
                myStorySnap.setTimestamp(photo.getTimestamp());
                myStorySnap.setTimingOut(photo.getTimingOut());
                try {
                    SnapChatDB.getInstance(PreviewActivity.this).saveMyStory(myStorySnap, userId);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Log.e("error",e.getMessage());
                }

                Log.e("image local save","finished");

                Log.e("image local num",""+SnapChatDB.getInstance(PreviewActivity.this).getMyStory(userId).size());

                Intent intent = new Intent(PreviewActivity.this, StoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
        TmpPhotoView.copy = null;
        //set the timer of the snap
        photo.setTimingOut(secondNumber);
        //set the timestamp of the snap
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        photo.setTimestamp(timestamp);
        //set the userid of the snap
        String email=mAuth.getInstance().getCurrentUser().getEmail();
        User user=db.findUserByEmail(email);
        photo.setUserId(user.getId());

        snapChatDB = SnapChatDB.getInstance(this);
        //set the photo path of the snap
        resetBase();
        StorageReference photoRef = mStorage.getInstance().getReference("Photos")
                .child(mAuth.getInstance().getCurrentUser().getUid())
                .child(timestamp.toString());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        base.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = photoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.i(TAG,"upload failed!!!");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                photo.setPath(taskSnapshot.getDownloadUrl().toString());
                photo.setSize(taskSnapshot.getBytesTransferred());
                taskSnapshot.getStorage();
                Log.i(TAG,"upload successful!!!");
                Log.i(TAG,String.valueOf(photo.getSize()));
                snapChatDB.Snap(photo);
            }
        });
    }

    public void downloadImagePublic() {
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
        Toast.makeText(getApplicationContext(), "download successfully", Toast.LENGTH_LONG).show();
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

