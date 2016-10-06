package com.example.wyyz.snapchat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;

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
                break;
            default:
                break;
        }
    }
    private void shareUsername(){
        final String sendText="Add me on Snapchat!\n Username: "+user.getUsername();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sendText);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
    private void shareQRcodewithUsername(){
        String codeStr=user.getQRcode();
        byte[] bytes= Base64.decode(codeStr,Base64.DEFAULT);
        Bitmap myBitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Uri codeUri=getImageUri(AddFriendsActivity.this,myBitmap);
        final String sendText="Add me on Snapchat!\n Username: "+user.getUsername();
        share(sendText,codeUri);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void share(String content, Uri uri){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if(uri!=null){
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            //get text by sms_body when user choose message
            shareIntent.putExtra("sms_body", content);
        }else{
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        //title
        startActivity(Intent.createChooser(shareIntent, "Share to"));
    }
}
