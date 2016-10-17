package com.example.wyyz.snapchat.activity;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * Activity to add friend by scan qr code
 */
public class ScanqrcodeActivity extends AppCompatActivity implements QRCodeView.Delegate {
    private static final String TAG = ScanqrcodeActivity.class.getSimpleName();
    private QRCodeView mQRCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanqrcode);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.startSpotAndShowRect();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }


    @Override
    public void onScanQRCodeSuccess(String result) {
        //check the validation of qr code
        char[] invalidChars={'.', '#', '$', '[', ']'};
        if(!containsNone(result,invalidChars)){
            onInvalidQRcode();
        }else {
            sendFriendRequestbyUid(result);
        }
        vibrate();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "Error occured when opening camera");
    }

    //send friend request
    private void sendFriendRequestbyUid(final String uid){
        final DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("Users");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(uid)){
                    DatabaseReference userRef=db.child(uid);
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("dataSnapSHot!!!",dataSnapshot.toString());
                                User user=dataSnapshot.getValue(User.class);
                                FriendRequestHelper.sendRequest(user);
                                final String showText="Scan successfully.\n Request sent, please wait for response.";
                                Toast.makeText(ScanqrcodeActivity.this, showText,
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                   onInvalidQRcode();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //helper method to check string doesn't contain any invalid character
    public static boolean containsNone(String str, char[] invalidChars) {
        if (str == null || invalidChars == null) {
            return true;
        }
        int strSize = str.length();
        int validSize = invalidChars.length;
        for (int i = 0; i < strSize; i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < validSize; j++) {
                if (invalidChars[j] == ch) {
                    return false;
                }
            }
        }
        return true;
    }
    private void onInvalidQRcode(){
        Toast.makeText(ScanqrcodeActivity.this, "Invalid Snapcode.",
                Toast.LENGTH_SHORT).show();
        mQRCodeView.startSpot();
    }
}
