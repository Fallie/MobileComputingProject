package com.example.wyyz.snapchat.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.util.TmpPhotoView;
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

/** This class is for taking picture and pass the temporary picture to preview activity for further
 *  edition. This class first check the permission of accessing camera is on in settings and request
 *  if is not. The switch of camera facing is also supported.
 * Created by Fallie on 25/09/2016.
 */

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "CameraActivity";
    //The code used for switch on the camera in settings.
    private static final int REQUEST_CAMERA = 0;
    private Camera camera;
    private Camera.Parameters parameters;
    //The temporary photo taken by this time.
    private Bitmap photo;
    //The current facing of the camera.
    private int currentCameraId;
    private Uri imageUri;
    private String imageUrl;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private boolean safeToTakePicture = false;
    //The chosen size list index of the pic taken by camera.
    private static final int chosenSize = 0;
    //If the permission of accessing camera is set to true or false.
    private boolean permission = false;

    /*Buttons and views used in the corresponding layout.*/
    Button btnTakePhoto;
    Button btnSwapCamera;
    Button btnSavePhoto;
    Button btnOpenSnap;
    Button btnProfile;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    View mLayout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_main);
        mLayout = findViewById(R.id.mainCameraView);
        //Check if the permission of accessing camera is on or off.
        checkPermission();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        Log.i(TAG, "Activity created!");
    }

    // Actions need to be done if permission is granted.
    public void actOnGranted(){
        surfaceView = (SurfaceView) findViewById(R.id.cameraSurface);
        surfaceView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                parameters = camera.getParameters();
                if (parameters.getMaxNumDetectedFaces() > 0) {
                    camera.startFaceDetection();
                    Toast.makeText(getApplicationContext(), "Face detection started",
                            Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        initialize();

    }

    //Check if the permission is granted.
    public void checkPermission() {
        Log.i(TAG, "Checking permission of camera usage.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            // Promote info to request for the permission.
            requestCameraPermission();
        } else {
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "CAMERA permission has already been granted. Displaying camera preview.");
            actOnGranted();
            permission = true;
        }
        // END_INCLUDE(camera_permission)

    }

    //Promote info to request permission.
    private void requestCameraPermission() {
        Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");
        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying camera permission rationale to provide additional context.");
            Snackbar.make(mLayout, "Camera permission is needed to show the camera preview.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(CameraActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
        // END_INCLUDE(camera_permission_request)
    }

    //Listen to the request when user clicks "allow" to grant the permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                permission = true;
                Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
                actOnGranted();
                Snackbar.make(mLayout, "Camera Permission has been granted.",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
                Snackbar.make(mLayout, "Permissions were not granted.",
                        Snackbar.LENGTH_SHORT).show();

            }
            // END_INCLUDE(permission_result)

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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

        btnProfile = (Button) findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(CameraActivity.this, ProfileActivity.class);
                startActivity(intent);
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
            public void onPictureTaken(byte[] bytes, final Camera camera) {
                photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                camera.startPreview();
                if (photo == null) {
                    safeToTakePicture = true;
                    return;
                }
                camera.stopPreview();
                Intent intent = new Intent(CameraActivity.this,PreviewActivity.class);
                Matrix matrix = new Matrix();
                if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
                    matrix.postRotate(90);
                else matrix.postRotate(270);
                Bitmap tmpPhoto = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(),
                        photo.getHeight(), matrix, true);
                TmpPhotoView.photo = tmpPhoto;
                //finish();
                startActivity(intent);
                //btnSavePhoto.setVisibility(View.VISIBLE);
                safeToTakePicture = true;


            }
        });

    }


    public void switchCamera() {

        if (camera != null) {
            camera.stopPreview();
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
        File dir = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES), "MyCameraApp");
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
        StorageReference photoRef = mStorage.getInstance().getReference("Photos")
                .child(mAuth.getInstance().getCurrentUser().getUid())
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
        if(camera!=null){camera.release();}
        Log.i(TAG, "Activity paused!");
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(permission) {
            camera = Camera.open(currentCameraId);
            camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
                @Override
                public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                    if (faces.length > 0) {
                        System.out.println("@ Location X " + faces[0].rect.centerX()
                                + "Location Y: " + faces[0].rect.centerY());
                    }
                }
            });
            parameters = camera.getParameters();
            List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();
            parameters.setPictureSize(sizeList.get(chosenSize).width,
                    sizeList.get(chosenSize).height);
            camera.setParameters(parameters);
            camera.setDisplayOrientation(90);
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
            safeToTakePicture = true;
            Log.i(TAG, "---------no camera on resume--------------");
        }
        Log.i(TAG, "Activity resumed!");
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
            @Override
            public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                if (faces.length > 0) {
                    System.out.println("@ Location X " + faces[0].rect.centerX()
                            + "Location Y: " + faces[0].rect.centerY());
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
        Log.i(TAG,"Activity stopped!");
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
        Log.i(TAG,"Activity stopped!");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        Log.i(TAG,"Activity destroyed!");
    }

}
