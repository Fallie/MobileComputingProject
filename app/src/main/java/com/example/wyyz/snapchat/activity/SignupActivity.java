package com.example.wyyz.snapchat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.glxn.qrgen.android.QRCode;

import java.io.ByteArrayOutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "SignupActivity";
    private SnapChatDB snapChatDB;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Bitmap myBitmap = null;
    private boolean isValid = false;

    @Bind(R.id.input_name)
    EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        snapChatDB = SnapChatDB.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        _signupButton.setOnClickListener(this);

        _loginLink.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        //signOut();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_signup){
            signup();
        }
        if(v.getId() == R.id.link_login){
            finish();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }

    private void createAccount(final String email, final String name, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validate()) {
            Log.d(TAG, "createAccount:" + "FAILED VALIDATION");
            return;
        }

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());



                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, R.string.register_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            isValid = true;
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference thisRef = database.getReference("Users");
                            String uid = mAuth.getCurrentUser().getUid();
                            DatabaseReference db = thisRef.child(uid);
                            myBitmap = QRCode.from(uid).bitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] bytes = baos.toByteArray();
                            String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
                            db.child("qrcode").setValue(base64Image);
                            db.child("username").setValue(name);
                            db.child("email").setValue(email);

                            createUser(email,name,base64Image);

                        }


                    }
                });
        // [END create_user_with_email]
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);


        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String name = _nameText.getText().toString().trim();
        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();

        createAccount(email,name,password);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();

                        // onSignupFailed();
                        if(progressDialog != null){
                        progressDialog.dismiss();}

                        if(isValid){
                            finish();
                            Intent intent = new Intent(SignupActivity.this,SnapActivity.class);
                            startActivity(intent);
                        }
                    }
                }, 3000);


    }

    private void createUser(String email, String name,String code) {

        if (isValid) {
            User currentUser = new User() {
            };
            currentUser.setEmail(email);
            currentUser.setUsername(name);
            currentUser.setQRcode(code);
            snapChatDB.saveUser(currentUser);

        }


    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public void signOut(){
        mAuth.getInstance().signOut();
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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



}
