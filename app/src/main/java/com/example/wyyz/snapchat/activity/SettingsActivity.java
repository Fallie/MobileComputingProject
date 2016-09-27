package com.example.wyyz.snapchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    RelativeLayout emailLayout;
    RelativeLayout usernameLayout;
    RelativeLayout birthdayLayout;
    RelativeLayout mobileLayout;
    RelativeLayout passwordLayout;
    TextView email;
    TextView username;
    TextView birthday;
    TextView mobile;
    String usernameContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        email=(TextView)findViewById(R.id.tv_email);
        username=(TextView)findViewById(R.id.tv_username);
        birthday=(TextView)findViewById(R.id.tv_birthday);
        mobile=(TextView)findViewById(R.id.tv_mobile);

        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user =dataSnapshot.getValue(User.class);
                email.setText(user.getEmail());
                username.setText(user.getUsername());
                if(dataSnapshot.hasChild("mobile")){
                    mobile.setText(user.getMobile());
                }else{
                    mobile.setText("");
                }
                if(dataSnapshot.hasChild("birthday")){
                    birthday.setText(user.getBirthday().toString());
                }else{
                    birthday.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        emailLayout =(RelativeLayout)findViewById(R.id.layout_email);
        usernameLayout =(RelativeLayout)findViewById(R.id.layout_name);
        birthdayLayout =(RelativeLayout)findViewById(R.id.layout_birthday);
        mobileLayout =(RelativeLayout)findViewById(R.id.layout_mobile);
        passwordLayout =(RelativeLayout)findViewById(R.id.layout_password);
        emailLayout.setOnClickListener(this);
        usernameLayout.setOnClickListener(this);
        birthdayLayout.setOnClickListener(this);
        mobileLayout.setOnClickListener(this);
        passwordLayout.setOnClickListener(this);
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
