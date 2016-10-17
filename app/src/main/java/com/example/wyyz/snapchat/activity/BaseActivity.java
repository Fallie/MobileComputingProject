package com.example.wyyz.snapchat.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.example.wyyz.snapchat.R;

import com.example.wyyz.snapchat.util.FirebaseUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    abstract void initComponents();

    abstract void addListeners();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpAuthenticationListener();
    }

    /**
     * set app theme
     */
    protected void setAppTheme(){

            setTheme(R.style.AppTheme);

    }



    /**
     * show error message
     * @param editText
     */
    public void showInputFieldError(EditText editText, String message){
        editText.requestFocus();
        editText.setError(message);
    }

    /**
     * set up Authentication Listener for FireBase
     */
    private void setUpAuthenticationListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseUtility.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "user is logged in " + user.getUid());
                } else {
                    Log.d(TAG, "user is logged out");
                }
            }
        };

        FirebaseUtility.getFireBaseAuthInstance().addAuthStateListener(mAuthStateListener);
    }

    /**
     * remove Auth State Listener
     */
    private void removeAuthStateListener(){
        if(mAuthStateListener != null){
            FirebaseUtility.getFireBaseAuthInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeAuthStateListener();
    }
}
