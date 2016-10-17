package com.example.wyyz.snapchat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.User;
import com.example.wyyz.snapchat.util.ConnectionDetector;
import com.example.wyyz.snapchat.util.ShowNetworkAlert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity to allow user check and edit profile information, signout
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    RelativeLayout emailLayout;
    RelativeLayout usernameLayout;
    RelativeLayout birthdayLayout;
    RelativeLayout mobileLayout;
    RelativeLayout passwordLayout;
    RelativeLayout signoutLayout;
    TextView email;
    TextView username;
    TextView birthday;
    TextView mobile;
    User currentUser;
    FirebaseUser user;
    SnapChatDB db;

    // Connection detector class
    private ConnectionDetector cd;
    // flag for Internet connection status
    private Boolean isInternetPresent = false;
    // Alert Dialog Manager
    private ShowNetworkAlert alert = new ShowNetworkAlert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cd = new ConnectionDetector(getApplicationContext());
        checkavailability();
        setContentView(R.layout.activity_settings);
        user= FirebaseAuth.getInstance().getCurrentUser();
        db=SnapChatDB.getInstance(SettingsActivity.this);

        email=(TextView)findViewById(R.id.tv_email);
        username=(TextView)findViewById(R.id.tv_username);
        birthday=(TextView)findViewById(R.id.tv_birthday);
        mobile=(TextView)findViewById(R.id.tv_mobile);

        emailLayout =(RelativeLayout)findViewById(R.id.layout_email);
        usernameLayout =(RelativeLayout)findViewById(R.id.layout_name);
        birthdayLayout =(RelativeLayout)findViewById(R.id.layout_birthday);
        mobileLayout =(RelativeLayout)findViewById(R.id.layout_mobile);
        passwordLayout =(RelativeLayout)findViewById(R.id.layout_password);
        signoutLayout = (RelativeLayout)findViewById(R.id.layout_signout);
        emailLayout.setOnClickListener(this);
        usernameLayout.setOnClickListener(this);
        birthdayLayout.setOnClickListener(this);
        mobileLayout.setOnClickListener(this);
        passwordLayout.setOnClickListener(this);
        signoutLayout.setOnClickListener(this);
    }
    protected void onResume() {
        super.onResume();
        currentUser=db.findUserByEmail(user.getEmail());
        email.setText(currentUser.getEmail());
        username.setText(currentUser.getUsername());
        birthday.setText(currentUser.getBirthday());
        mobile.setText(currentUser.getMobile());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_name:
                Intent intent2=new Intent(SettingsActivity.this,ProfileEditingActivity.class);
                intent2.putExtra("item",ProfileEditingActivity.USERNAME);
                intent2.putExtra("content",currentUser.getUsername());
                startActivity(intent2);
                break;
            case R.id.layout_birthday:
                Intent intent3=new Intent(SettingsActivity.this,ProfileEditingActivity.class);
                intent3.putExtra("item",ProfileEditingActivity.BIRTHDAY);
                intent3.putExtra("content",currentUser.getBirthday());
                startActivity(intent3);
                break;
            case R.id.layout_mobile:
                Intent intent4=new Intent(SettingsActivity.this,ProfileEditingActivity.class);
                intent4.putExtra("item",ProfileEditingActivity.MOBILE);
                intent4.putExtra("content",currentUser.getMobile());
                startActivity(intent4);
                break;
            case R.id.layout_password:
                Intent intent5=new Intent(SettingsActivity.this,ProfileEditingActivity.class);
                intent5.putExtra("item",ProfileEditingActivity.PASSWORD);
                startActivity(intent5);
                break;
            case R.id.layout_signout:
                doSignout();
            default:
                break;
        }
    }

    //check network availability
    public void checkavailability() {
        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(SettingsActivity.this,
                    "Fail",
                    "Internet Connection is NOT Available", false);
            // stop executing code by return
            return;
        }
    }

    //signout
    private void doSignout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Wanna sign out?");
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();    }
}
