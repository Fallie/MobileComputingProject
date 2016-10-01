package com.example.wyyz.snapchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.wyyz.snapchat.R;

public class AddFriendsActivity extends AppCompatActivity implements View.OnClickListener{
    RelativeLayout addFriendsByUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        addFriendsByUsername=(RelativeLayout)findViewById(R.id.layout_by_username);
        addFriendsByUsername.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_by_username:
                Intent itent=new Intent(AddFriendsActivity.this,AddFriendByUsernameActivity.class);
                startActivity(itent);
                break;
            default:
                break;
        }
    }
}
