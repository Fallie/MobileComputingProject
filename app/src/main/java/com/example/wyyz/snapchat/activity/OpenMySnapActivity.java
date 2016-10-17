package com.example.wyyz.snapchat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.util.ConnectionDetector;
import com.example.wyyz.snapchat.util.ShowNetworkAlert;
import com.example.wyyz.snapchat.util.TmpPhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by Fallie on 15/10/2016.
 */

public class OpenMySnapActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "OpenMySnapActivity";
    private FirebaseStorage mStorage;
    private SnapChatDB snapChatDB;
    private Uri uri;
    private FirebaseAuth mAuth;
    private Button deleteSnap;
    private Button nextStep;
    private Button editSnap;
    private Button shareSnap;
    private ImageView imageView;
    private MenuInflater inflater;
    private String timeStamp;
    private long size;
    // Connection detector class
    private ConnectionDetector cd;
    // flag for Internet connection status
    private Boolean isInternetPresent = false;
    // Alert Dialog Manager
    private ShowNetworkAlert alert = new ShowNetworkAlert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cd = new ConnectionDetector(getApplicationContext());
        checkavailability();
        Intent intent = getIntent();
        uri = Uri.parse(intent.getStringExtra("SnapPath"));
        timeStamp = intent.getStringExtra("TimeStamp");
        size = intent.getLongExtra("Size",0);
        setContentView(R.layout.activity_openmysnap);
        initialize();
        Log.i(TAG, "Activity created!");
    }

    private void initialize() {
        snapChatDB = SnapChatDB.getInstance(this);
        final TextView secondNum = (TextView) findViewById(R.id.secondNumber);
        imageView = (ImageView) findViewById(R.id.previewImage);
        //imageView.setBackgroundDrawable(new BitmapDrawable(base));
        Glide.with(getBaseContext())
                .load(uri.toString())
                .into(imageView);
        nextStep = (Button) findViewById(R.id.btnNextStep);
        nextStep.setOnClickListener(this);
        deleteSnap = (Button) findViewById(R.id.btnDelete);
        deleteSnap.setOnClickListener(this);
        editSnap = (Button) findViewById(R.id.btnEditSnap);
        editSnap.setOnClickListener(this);
        shareSnap = (Button) findViewById(R.id.btnShareSnap);
        shareSnap.setOnClickListener(this);

    }

    public void checkavailability() {
        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(OpenMySnapActivity.this,
                    "Fail",
                    "Internet Connection is NOT Available", false);
            // stop executing code by return
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.openmysnap_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_myeyes:
                lockSnap();
                return true;
            case R.id.action_creatstory:
                createStory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createStory() {
        Intent intent = new Intent(OpenMySnapActivity.this,DisplaySnapActivity.class);
        ArrayList<String> str = new ArrayList<String>();
        str.add(uri.toString());
        int[] timer = {3};

        intent.putExtra("SnapPath",str);
        intent.putExtra("Timer",timer);
        startActivity(intent);
    }

    private void lockSnap() {

        snapChatDB.lockSnapByUri(uri.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNextStep:
                finish();
                //Intent intent = new Intent(PreviewActivity.this,CameraActivity.class);
                //startActivity(intent);
                //finishSettingSnap();
                break;
            case R.id.btnDelete:
                popDelete();
                break;
            case R.id.btnEditSnap:
                editSnap();
                break;
            case R.id.btnShareSnap:
                shareSnap("",uri);
                break;
            default:
                break;

        }
    }

    private void editSnap() {
        StorageReference islandRef = mStorage.getInstance().getReferenceFromUrl(uri.toString());


        islandRef.getBytes(size).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                TmpPhotoView.photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Intent intent = new Intent(OpenMySnapActivity.this,PreviewActivity.class);
                intent.putExtra("SnapPath",uri);
                intent.putExtra("TimeStamp",timeStamp);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e(TAG,"Fail to download to tmp file");
            }
        });
    }


    private void popDelete(){
        //pop alert dialog
        AlertDialog.Builder builder1 = new AlertDialog.Builder(OpenMySnapActivity.this);
        builder1.setTitle("Wanna Delete?");
        // Set up the buttons
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteSnap();
                Log.i(TAG,"Start deleting text");
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
    private void deleteSnap() {
        //1.delete the photo on firebase
        StorageReference storageRef = mStorage.getInstance().getReferenceFromUrl("gs://mysnapchat-a20fe.appspot.com/");

        // Create a reference to the file to delete
        StorageReference desertRef = storageRef.child("Photos/").child(mAuth.getInstance().getCurrentUser().getUid())
                .child(timeStamp);

        desertRef.delete().addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Intent intent = new Intent(OpenMySnapActivity.this,SnapActivity.class);
                startActivity(intent);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.e(TAG,"fail to delete photo");
            }
        });


        //2.delete the snap locally
        snapChatDB.deleteSnapByPath(uri.toString());

    }

    private void shareSnap(String content, Uri uri){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if(uri!=null){
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            //get text by sms_body when user choose message
            shareIntent.putExtra("sms_body", content);
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        //title
        startActivity(Intent.createChooser(shareIntent, "Share to"));
    }



}


