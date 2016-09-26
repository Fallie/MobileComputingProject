package com.example.wyyz.snapchat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.wyyz.snapchat.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Fallie on 25/09/2016.
 */

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "CameraActivity";
    private Camera camera;
    private Camera.Parameters parameters;
    private Bitmap photo;
    private int currentCameraId;
    private Uri imageUri;
    private String imageUrl;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private static final int PICTURE_RESULT = 9000;
    private boolean safeToTakePicture = false;
    private static final int chosenSize = 0;
    Button btnTakePhoto;
    Button btnSwapCamera;
    Button btnSavePhoto;
    Button btnOpenSnap;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_main);
        surfaceView = (SurfaceView) findViewById(R.id.cameraSurface);
        surfaceView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                parameters = camera.getParameters();
                if (parameters.getMaxNumDetectedFaces() > 0) {
                    camera.startFaceDetection();
                    Toast.makeText(getApplicationContext(), "Face detection started", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        initialize();
        Log.i(TAG, "Activity created!");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void initialize() {

        btnTakePhoto = (Button) findViewById(R.id.btnButton);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (safeToTakePicture) {
                    takePhoto();
                    safeToTakePicture = false;
                } else Log.i(TAG, "fail to take photos.");
            }
        });


        btnSwapCamera = (Button) findViewById(R.id.btnFront);
        btnSwapCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });

        btnSavePhoto = (Button) findViewById(R.id.btnSave);
        btnSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadImagePublic();
            }
        });

        btnOpenSnap = (Button) findViewById(R.id.btnOpenSnap);
        btnOpenSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(CameraActivity.this, SnapActivity.class);
                startActivity(intent);
            }
        });


    }

    public void takePhoto() {

        camera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                camera.startPreview();
                if (photo == null) {
                    safeToTakePicture = true;
                    return;
                }
                camera.stopPreview();
                btnSavePhoto.setVisibility(View.VISIBLE);
                safeToTakePicture = true;
            }
        });

    }


    public void switchCamera() {
        camera.stopPreview();
        if (camera != null) {
            camera.release();
            camera = null;
        }

        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        camera = Camera.open(currentCameraId);
        camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    public void downloadImagePublic() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!dir.exists()) {
            dir.mkdir();
        }


        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(dir.getPath() + File.separator + "IMG_" + timestamp + ".jpg");


        imageUri = Uri.fromFile(mediaFile);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
        FileOutputStream outputStream = null;


        try {
            outputStream = new FileOutputStream(mediaFile);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            uploadImage(imageUri);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mediaFile)));
        Toast.makeText(getApplicationContext(), "download successfully", Toast.LENGTH_LONG).show();
    }

    private void uploadImage(Uri imageUri) {
        // Get a reference to store file at photos/<FILENAME>.jpg
        StorageReference photoRef = mStorage.getInstance().getReference("Photos").child(mAuth.getInstance().getCurrentUser().getUid())
                .child(imageUri.getLastPathSegment());
        // [END get_child_ref]

        // Upload file to Firebase Storage
        Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
        photoRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Upload succeeded
                        Log.d(TAG, "uploadFromUri:onSuccess");

                        // Get the public download URL
                        //Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();

                        // [START_EXCLUDE]


                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);

                        // [START_EXCLUDE]


                        // [END_EXCLUDE]
                    }
                });
    }


    @Override
    protected void onPause() {
        super.onPause();
        camera.release();
        Log.i(TAG, "Activity paused!");
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
            @Override
            public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                if (faces.length > 0) {
                    System.out.println("@ Location X " + faces[0].rect.centerX() + "Location Y: " + faces[0].rect.centerY());
                }
            }
        });
        parameters = camera.getParameters();
        List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();
        parameters.setPictureSize(sizeList.get(chosenSize).width, sizeList.get(chosenSize).height);
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
        Log.i(TAG, "Surface created!");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        parameters = camera.getParameters();
        List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();
        parameters.setPictureSize(sizeList.get(chosenSize).width, sizeList.get(chosenSize).height);
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        camera.startPreview();
        safeToTakePicture = true;
        Log.i(TAG, "Surface changed!");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        Log.i(TAG, "Surface destroyed!");
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Camera Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
