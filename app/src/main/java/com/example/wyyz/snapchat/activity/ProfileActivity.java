package com.example.wyyz.snapchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
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

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    TextView _nickName;
    ImageView settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        _nickName=(TextView)findViewById(R.id.tv_nickname);

        FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user =dataSnapshot.getValue(User.class);
                _nickName.setText(user.getUsername());
               /* if(dataSnapshot.hasChild("usernameLayout")){
                    Log.d("HEY!!!","TRUE!");
                }
                if(dataSnapshot.hasChild("mobileLayout")){
                    Log.d("YEAH?!","aaa");
                }else{
                    Log.d("NO mobileLayout yet","bbb");
                }*/
                //Log.d("mobileLayout???",user.getMobile());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
