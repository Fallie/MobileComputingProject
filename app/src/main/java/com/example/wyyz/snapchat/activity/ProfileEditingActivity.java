package com.example.wyyz.snapchat.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ProfileEditingActivity extends AppCompatActivity {
    public static final int USERNAME=1;
    public static final int BIRTHDAY=2;
    public static final int MOBILE=3;
    public static final int PASSWORD=4;
    TextView title;
    Button save;
    EditText editText;
    TextView tvCurrentPwd;
    TextView tvNewPwd;
    EditText newPwd;
    TextView tvConfirmPwd;
    EditText confirmPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editing);
        title=(TextView)findViewById(R.id.tv_title);
        save=(Button)findViewById(R.id.btn_save);
        editText=(EditText)findViewById(R.id.edit_text1);
        Intent intent=getIntent();
        int item=intent.getIntExtra("item",1);
        switch (item){
            case USERNAME:
                title.setText("Email");
                setText(intent);
                saveUsername();
                break;
            case BIRTHDAY:
                title.setText("Birthday");
                setText(intent);
                saveBirthday();
                break;
            case MOBILE:
                title.setText("Mobile Number");
                setText(intent);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                saveMobile();
                break;
            case PASSWORD:
                title.setText("Password");
                savePassword();
                break;
            default:
                break;
        }
    }

    private void saveUsername(){
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update database
                FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
                userRef.child("username").setValue(editText.getText().toString());
                updateProfileinLocalDB(currentUser,USERNAME,editText.getText().toString());
                ProfileEditingActivity.this.finish();
            }
        });

    }
    private void saveBirthday() {

        editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(ProfileEditingActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        editText.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update database
                FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
                userRef.child("birthday").setValue(editText.getText().toString());
                updateProfileinLocalDB(currentUser,BIRTHDAY,editText.getText().toString());
                ProfileEditingActivity.this.finish();
            }
        });
    }
    private void saveMobile(){
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update database
                FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
                userRef.child("mobile").setValue(editText.getText().toString());
                updateProfileinLocalDB(currentUser,MOBILE,editText.getText().toString());
                ProfileEditingActivity.this.finish();
            }
        });
    }
    private void savePassword(){
        tvCurrentPwd=(TextView)findViewById(R.id.tv_currentpwd);
        tvCurrentPwd.setVisibility(View.VISIBLE);
        tvNewPwd=(TextView)findViewById(R.id.tv_newpwd);
        tvNewPwd.setVisibility(View.VISIBLE);
        newPwd=(EditText)findViewById(R.id.et_newpwd);
        newPwd.setVisibility(View.VISIBLE);
        tvConfirmPwd=(TextView)findViewById(R.id.tv_confirmpwd);
        tvConfirmPwd.setVisibility(View.VISIBLE);
        confirmPwd=(EditText)findViewById(R.id.et_confirmpwd);
        confirmPwd.setVisibility(View.VISIBLE);
        editText.setInputType(129);
        newPwd.setInputType(129);
        confirmPwd.setInputType(129);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check passwordLayout
                //update database
                String oldPassword=editText.getText().toString();
                String newPassword=newPwd.getText().toString();
                String confirmPassword=confirmPwd.getText().toString();
                if(validate(oldPassword,newPassword,confirmPassword)){
                    FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
                    currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileEditingActivity.this, "User password successfully updated.",
                                        Toast.LENGTH_LONG).show();
                                ProfileEditingActivity.this.finish();
                            }else{
                                Toast.makeText(ProfileEditingActivity.this, "Task failed.",
                                        Toast.LENGTH_LONG).show();
                                ProfileEditingActivity.this.finish();
                            }
                        }
                    });
                }
            }
        });
    }
    private void setText(Intent intent){
        String content=intent.getStringExtra("content");
        editText.setText(content);
    }

    private boolean validate(String oldPassword, String newPassword,String confirmPassword){
        boolean validate=true;
        if(oldPassword.isEmpty()){
            editText.setError("Cannot be empty.");
            validate=false;
        }
        if(newPassword.isEmpty()){
            newPwd.setError("Cannot be empty.");
            validate=false;
        }
        if(confirmPassword.isEmpty()){
            confirmPwd.setError("Cannot be empty.");
            validate=false;
        }
        if(!newPassword.equals(confirmPassword)){
            confirmPwd.setError("Not equal.");
            validate=false;
        }
        return validate;
    }
    private void updateProfileinLocalDB(FirebaseUser user,int item, String content){
        String email=user.getEmail();
        SnapChatDB db=SnapChatDB.getInstance(ProfileEditingActivity.this);
        switch (item){
            case USERNAME:
                db.updateUsername(email,content);
                break;
            case MOBILE:
                db.updateUserMobile(email,content);
                break;
            case BIRTHDAY:
                db.updateUserBirthday(email,content);
                break;
            default:
                break;
        }
    }



}

