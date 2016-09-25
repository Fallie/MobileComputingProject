package com.example.wyyz.snapchat.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wyyz.snapchat.R;

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
                saveUsername();
                break;
            case BIRTHDAY:
                title.setText("Birthday");
                saveBirthday();
                break;
            case MOBILE:
                title.setText("Mobile Number");
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
                editText.setText("UserNameSAVED!!!!!");//used for testing
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
                editText.setText("UserNameSAVED!!!!!");
            }
        });
    }
    private void saveMobile(){
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update database
                editText.setText("MobileSAVED!!!!!");
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check password
                //update database
                editText.setText("PasswordSAVED!!!!!");
            }
        });
    }
    }

