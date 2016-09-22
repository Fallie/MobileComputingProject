package com.example.wyyz.snapchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.ChatRecord;
import com.example.wyyz.snapchat.model.Friend;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private Friend friend;
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter adapter;
    private List<ChatRecord> msgList=new ArrayList<ChatRecord>();
    //TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_chat);
       // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        Intent intent=getIntent();
        String jsonFriend=intent.getStringExtra("friend");
        Gson gson=new Gson();
        friend=gson.fromJson(jsonFriend,Friend.class);
       // tvTitle=(TextView)findViewById(R.id.tv_title);
        testingMsgs();
        adapter=new MsgAdapter(ChatActivity.this,R.layout.msg_item,msgList,friend);
        inputText=(EditText) findViewById(R.id.input_text);
        send=(Button) findViewById(R.id.send);
        msgListView=(ListView)findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=inputText.getText().toString();
                if(!"".equals(content)){
                    ChatRecord msg=new ChatRecord();
                    msg.setContent(content);
                    msg.setReceiverId(friend.getFriendId());
                    msg.setSenderId(friend.getOwnerId());
                    msg.setTimestamp(new Date());
                    msg.setType(ChatRecord.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size());
                    inputText.setText("");
                }
            }
        });

    }
    private void testingMsgs(){
        ChatRecord msg1=new ChatRecord();
        msg1.setTimestamp(new Date());
        msg1.setType(ChatRecord.TYPE_RECEIVED);
        msg1.setSenderId(friend.getFriendId());
        msg1.setReceiverId(friend.getOwnerId());
        msg1.setContent("Hello!");
        msgList.add(msg1);
        ChatRecord msg2=new ChatRecord();
        msg2.setTimestamp(new Date());
        msg2.setType(ChatRecord.TYPE_RECEIVED);
        msg2.setSenderId(friend.getFriendId());
        msg2.setReceiverId(friend.getOwnerId());
        msg2.setContent("Good Morning!");
        msgList.add(msg2);
    }
}
