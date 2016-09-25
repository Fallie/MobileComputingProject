package com.example.wyyz.snapchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.wyyz.snapchat.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    RelativeLayout email;
    RelativeLayout username;
    RelativeLayout birthday;
    RelativeLayout mobile;
    RelativeLayout password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        email=(RelativeLayout)findViewById(R.id.layout_email);
        username=(RelativeLayout)findViewById(R.id.layout_name);
        birthday=(RelativeLayout)findViewById(R.id.layout_birthday);
        mobile=(RelativeLayout)findViewById(R.id.layout_mobile);
        password=(RelativeLayout)findViewById(R.id.layout_password);
        email.setOnClickListener(this);
        username.setOnClickListener(this);
        birthday.setOnClickListener(this);
        mobile.setOnClickListener(this);
        password.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_name:
                Intent intent2=new Intent(SettingsActivity.this,ProfileEditingActivity.class);
                intent2.putExtra("item",ProfileEditingActivity.USERNAME);
                startActivity(intent2);
                break;
            case R.id.layout_birthday:
                Intent intent3=new Intent(SettingsActivity.this,ProfileEditingActivity.class);
                intent3.putExtra("item",ProfileEditingActivity.BIRTHDAY);
                startActivity(intent3);
                break;
            case R.id.layout_mobile:
                Intent intent4=new Intent(SettingsActivity.this,ProfileEditingActivity.class);
                intent4.putExtra("item",ProfileEditingActivity.MOBILE);
                startActivity(intent4);
                break;
            case R.id.layout_password:
                Intent intent5=new Intent(SettingsActivity.this,ProfileEditingActivity.class);
                intent5.putExtra("item",ProfileEditingActivity.PASSWORD);
                startActivity(intent5);
                break;
            default:
                break;
        }
    }
}
