package com.example.wyyz.snapchat.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.Snap;
import com.example.wyyz.snapchat.util.TmpPhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Created by Fallie on 27/09/2016.
 */

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "PreviewActivity";
    private Snap photo = new Snap();
    private Uri imageUri;
    private String imageUrl;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private static final int chosenSize = 0;
    private int secondNumber = 3;

    Button returnCamera;
    Button nextStep;
    Button saveToMemory;
    Button timer;
    Button addToStory;
    Button addPicto;
    Button addNote;
    Button draw;
    TextView secondNum;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        //photo.setUserId(mAuth.getInstance().getCurrentUser().getUid());
        //photo.setPhoto(tmpPhotoView.photo);

        initialize();
        secondNum = (TextView) findViewById(R.id.secondNumber);



        Log.i(TAG, "Activity created!");
    }

    private void initialize() {

        imageView = (ImageView) findViewById(R.id.previewImage);
        imageView.setImageBitmap(TmpPhotoView.photo);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReturnCamera:
                finish();
                Intent intent = new Intent(PreviewActivity.this,CameraActivity.class);
                startActivity(intent);
                break;
            case R.id.btnNextStep:
                break;
            case R.id.btnTimer:
                setTimer();
                break;
            case R.id.btnAddToStory:
                addPhotoToStory();
                break;
            case R.id.btnAddPicto:
                addPictogram();
                break;
            case R.id.btnAddNote:
                addText();
                break;
            case R.id.btnSaveToMemory:
                savePhotoToMemory();
                break;
            case R.id.btnDraw:
                startDraw();
                break;
        }
    }

    private void startDraw() {
    }

    private void savePhotoToMemory() {
    }

    private void addText() {
    }

    private void addPictogram() {
    }

    private void addPhotoToStory() {
    }

    private void setTimer() {

    }
}
