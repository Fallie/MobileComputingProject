package com.example.wyyz.snapchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    TextView addfriends;
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
        addfriends=(TextView)findViewById(R.id.tv_addfriend);
        addfriends.setOnClickListener(this);
    }

    protected void onResume(){
        super.onResume();
        currentUser=db.findUserByEmail(user.getEmail());
        _nickName.setText(currentUser.getUsername());
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
            default:
                break;
        }
    }

}
