package com.example.wyyz.snapchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wyyz.snapchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TextView _nickName;
    ImageView settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        Log.d("currentUser:", currentUser.toString());
        _nickName=(TextView)findViewById(R.id.tv_nickname);
        _nickName.setText(currentUser.getEmail());
        Log.d("email:",currentUser.getEmail());
        //Log.d("name:",currentUser.getDisplayName());
        settings=(ImageView)findViewById(R.id.imgv_settings);
        settings.setOnClickListener(this);
       
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgv_settings:
                Intent intent=new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
