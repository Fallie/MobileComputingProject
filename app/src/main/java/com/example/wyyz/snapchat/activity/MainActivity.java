package com.example.wyyz.snapchat.activity;

/**
 * Created by Fallie on 21/09/2016.
 */


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.User;
import com.example.wyyz.snapchat.util.ConnectionDetector;
import com.example.wyyz.snapchat.util.ShowNetworkAlert;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_SIGNUP = 0;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean isValid = false;
    private SnapChatDB snapChatDB;
    private boolean autoLogin;
    private ProgressDialog progressDialog;
    // Connection detector class
    private ConnectionDetector cd;
    // flag for Internet connection status
    private Boolean isInternetPresent = false;
    // Alert Dialog Manager
    private ShowNetworkAlert alert = new ShowNetworkAlert();

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password)EditText _passwordText;
    @Bind(R.id.btn_login)Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cd = new ConnectionDetector(getApplicationContext());
        checkavailability();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        autoLogin = false;

        progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme);

        _loginButton.setOnClickListener(this);

        _signupLink.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    autoLogin = true;
                    Intent intent = new Intent(MainActivity.this,CameraActivity.class);
                    startActivity(intent);
                    mAuth.removeAuthStateListener(mAuthListener);
                    finish();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };
    }

    public void checkavailability() {
        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(MainActivity.this,
                    "Fail",
                    "Internet Connection is NOT Available", false);
            // stop executing code by return
            return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG,"%%%%Here it started%%%%%%%%");
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG,"%%%%Here it stopped%%%%%%%%");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onPause(){
        super.onPause();
        progressDialog.dismiss();
        Log.i(TAG,"%%%%Here it paused%%%%%%%%");
    }


    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.btn_login){
            login();
        }
        if(v.getId() == R.id.link_signup){
            finish();
            Intent intent = new Intent(this,SignupActivity.class);
            startActivity(intent);
        }
    }
    private void login() {
        Log.d(TAG,"Login");

        if(!validate()){
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);


        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();


        // TODO: Implement your own authentication logic here.
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }



                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            onLoginFailed();
                        }else {
                            isValid = true;
                        }

                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        if(progressDialog != null){
                        progressDialog.dismiss();}
                        if(isValid){
                            syncUser();
                            if(autoLogin == false){
                            Intent intent = new Intent(MainActivity.this,CameraActivity.class);
                            finish();
                            startActivity(intent);
                            }
                        }
                    }
                }, 3000);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful SignupActivity logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    private void syncUser(){
        snapChatDB=SnapChatDB.getInstance(MainActivity.this);
        User user=snapChatDB.findUserByEmail(_emailText.getText().toString());
        Log.d("Can I get text??", _emailText.getText().toString());
        if(user==null){
            FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User remoteUser=dataSnapshot.getValue(User.class);
                    snapChatDB.saveUser(remoteUser);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
