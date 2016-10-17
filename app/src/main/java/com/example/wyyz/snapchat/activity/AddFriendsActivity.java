package com.example.wyyz.snapchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity to add new friends, provide three kinds of methods
 */
public class AddFriendsActivity extends AppCompatActivity implements View.OnClickListener{
    RelativeLayout addFriendsByUsername;
    RelativeLayout shareUsername;
    RelativeLayout addByQRcode;
    SnapChatDB db;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        addFriendsByUsername=(RelativeLayout)findViewById(R.id.layout_by_username);
        addFriendsByUsername.setOnClickListener(this);
        shareUsername=(RelativeLayout)findViewById(R.id.layout_share_username);
        shareUsername.setOnClickListener(this);
        addByQRcode=(RelativeLayout)findViewById(R.id.layout_by_qrcode);
        addByQRcode.setOnClickListener(this);
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        db=SnapChatDB.getInstance(AddFriendsActivity.this);
        user=db.findUserByEmail(currentUser.getEmail());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_by_username:
                Intent itent=new Intent(AddFriendsActivity.this,AddFriendByUsernameActivity.class);
                startActivity(itent);
                break;
            case R.id.layout_share_username:
                shareUsername();
                break;
            case R.id.layout_by_qrcode:
                Intent itent1=new Intent(AddFriendsActivity.this,ScanqrcodeActivity.class);
                startActivity(itent1);
                break;
            default:
                break;
        }
    }
    //social share username to other app
    private void shareUsername(){
        final String sendText="Add me on Snapchat!\n Username: "+user.getUsername();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sendText);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

}
