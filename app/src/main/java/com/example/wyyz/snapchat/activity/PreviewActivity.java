package com.example.wyyz.snapchat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.Snap;
import com.example.wyyz.snapchat.util.TmpPhotoView;
import com.example.wyyz.snapchat.util.TmpText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

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
    private Canvas canvas;
    private Bitmap base;
    private Bitmap alteredBitmap ;
    private Paint paint;
    private RelativeLayout layout;
    private int flagOnTouch = 0;
    Button returnCamera;
    Button nextStep;
    Button saveToMemory;
    Button timer;
    Button addToStory;
    Button addPicto;
    Button addNote;
    Button draw;
    ImageView imageView;
    NumberPicker np;


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
        imageView = (ImageView) findViewById(R.id.previewImage);
        imageView.setImageBitmap(base);
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

    protected void resumeCanvas() {

        Toast.makeText(PreviewActivity.this, "The canvas is cleared!",Toast.LENGTH_SHORT).show();
    }

    private void startDraw() {
        layout.addView(new TouchEventView(this,null,1));
        //setContentView(new TouchEventView(this,null));

        Log.e(TAG,"Start building canvas");
    }

    private void savePhotoToMemory() {
    }

    private void addText() {

        TmpText.textContent = "Hello World!";
        layout.addView(new DrawTextView(this,null,3));

    }

    private void addPictogram() {
        layout.addView(new AddEmoticonView(this,null,2));
    }

    private void addPhotoToStory() {
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


}

