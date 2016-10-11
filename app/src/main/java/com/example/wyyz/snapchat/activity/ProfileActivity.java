package com.example.wyyz.snapchat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    TextView _nickName;
    ImageView settings;
    ImageView cameraOpening;
    TextView addfriends;
    TextView addedme;
    TextView myfriends;
    ImageView snapCode;
    FirebaseUser user;
    User currentUser;
    SnapChatDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user= FirebaseAuth.getInstance().getCurrentUser();
        db=SnapChatDB.getInstance(ProfileActivity.this);
        _nickName=(TextView)findViewById(R.id.tv_nickname);
        settings=(ImageView)findViewById(R.id.imgv_settings);
        settings.setOnClickListener(this);
        cameraOpening=(ImageView)findViewById(R.id.imageView3);
        cameraOpening.setOnClickListener(this);
        addfriends=(TextView)findViewById(R.id.tv_addfriend);
        addfriends.setOnClickListener(this);
        addedme=(TextView) findViewById(R.id.tv_addedme);
        addedme.setOnClickListener(this);
        myfriends=(TextView)findViewById(R.id.tv_myfriends);
        myfriends.setOnClickListener(this);

        //QRcode
        snapCode=(ImageView)findViewById(R.id.imgv_snapcode);
    }

    protected void onResume(){
        super.onResume();
        currentUser=db.findUserByEmail(user.getEmail());
        _nickName.setText(currentUser.getUsername());
        String codeStr=currentUser.getQRcode();
        byte[] bytes= Base64.decode(codeStr,Base64.DEFAULT);
        Bitmap myBitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        snapCode.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 300, 300, false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgv_settings:
                Intent intent=new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_addfriend:
                Intent intent1=new Intent(ProfileActivity.this, AddFriendsActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_addedme:
                Intent intent2=new Intent(ProfileActivity.this, AddedmeActivity.class);
                startActivity(intent2);
                break;
            case R.id.imageView3:
                Intent intent3=new Intent(ProfileActivity.this, CameraActivity.class);
                startActivity(intent3);
                break;
            case R.id.tv_myfriends:
                Intent intent4=new Intent(ProfileActivity.this, MyfriendsActivity.class);
                startActivity(intent4);
                break;
            default:
                break;
        }
    }

}
