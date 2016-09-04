package com.example.wyyz.snapchat.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;

public class MainActivity extends AppCompatActivity {

    private SnapChatDB snapChatDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        snapChatDB = SnapChatDB.getInstance(this);
    }
}
