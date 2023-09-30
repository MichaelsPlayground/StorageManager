package de.androidcrypto.storagemanager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TakePhotoActivity extends AppCompatActivity {
    private static final String TAG = TakePhotoActivity.class.getSimpleName();
    private AutoCompleteTextView chooseUnitNumber;
    private String selectedUnitNumber;
    private DBUnitHandler dbUnitHandler;


    // take and crop images
    private Button takePhoto, cropImageDefault, cropImageChose, useUncroppedImage;
    private ImageView ivFull, ivCrop;
    private TextView tvFull, tvCrop;
    private final String FIXED_IMAGE_EXTENSION = ".jpg";

    private final String CACHE_FOLDER = "crop";
    private String intermediateName = "1.jpg";
    private String resultName = "2.jpg";

    private final String FILE_PROVIDER_AUTHORITY = "de.androidcrypto.storagemanager";
    private Uri intermediateProvider;
    private Uri resultProvider;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaActivityResultLauncher;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private ActivityResultLauncher<Intent> cropActivityResultLauncher;

    private Uri imageUriFull, imageUriCrop;
    private boolean useFixedCropper = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        takePhoto = findViewById(R.id.btnTakePhoto);
        cropImageDefault = findViewById(R.id.btnCropImageDefault);
        cropImageChose = findViewById(R.id.btnCropImageChose);
        useUncroppedImage = findViewById(R.id.btnUseUncroppedImage);

        ivFull = findViewById(R.id.ivFull);
        ivCrop = findViewById(R.id.ivCrop);
        tvFull = findViewById(R.id.tvFull);
        tvCrop = findViewById(R.id.tvCrop);

        //checkForImageCropper();
        askPermissions();

        dbUnitHandler = new DBUnitHandler(TakePhotoActivity.this);
        selectedUnitNumber = "";

        chooseUnitNumber = findViewById(R.id.chooseUnitNumber);
        String[] unitNumbers = dbUnitHandler.getUnitNumbers();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.drop_down_item,
                unitNumbers);
        chooseUnitNumber.setAdapter(arrayAdapter);
        chooseUnitNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUnitNumber = chooseUnitNumber.getText().toString();
                Log.d(TAG, "selected unit number: " + selectedUnitNumber);
                // take a photo, crop it and store it

                // radio group for selecting image 1 / 2 / 3

            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "takePhoto");
                onLaunchCamera();
            }
        });

/**
 * section for ActivityResultLauncher
 */

        // android 13 photo picker
        // Registers a photo picker activity launcher in single-select mode.
        // https://developer.android.com/training/data-storage/shared/photopicker
        pickMediaActivityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        imageUriFull = uri;
                        saveBitmapFileToIntermediate(imageUriFull);

                        Bitmap inputImage = loadFromUri(intermediateProvider);
                        Bitmap rotated = rotateBitmap(getResizedBitmap(inputImage, 800), imageUriFull);
                        ivFull.setImageBitmap(rotated);

                        int height = ivFull.getHeight();
                        int width = ivFull.getWidth();

                        //Bitmap inputImage = uriToBitmap(imageUriFull);
                        String imageInfo = "height: " + height + " width: " + width + " resolution: " + (height * width) +
                                "\nOriginal Bitmap height: " + inputImage.getHeight() + " width: " + inputImage.getWidth() +
                                " res: " + (inputImage.getHeight() * inputImage.getWidth());
                        tvFull.setText(imageInfo);
                    } else {
                        Log.d(TAG, "No media selected");
                    }
                });

        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageUriFull = intermediateProvider;
                        Bitmap inputImage = loadFromUri(intermediateProvider);
                        Bitmap rotated = rotateBitmap(getResizedBitmap(inputImage, 800), getCameraOrientation());
                        ivFull.setImageBitmap(rotated);
                        String imageInfo = "Bitmap height: " + inputImage.getHeight() + " width: " + inputImage.getWidth() +
                                " res: " + (inputImage.getHeight() * inputImage.getWidth());
                        tvFull.setText(imageInfo);
                        saveBitmapFileToIntermediate(inputImage);
                    }
                });

        cropActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageUriCrop = resultProvider;
                        Bitmap cropImage = loadFromUri(resultProvider);
                        ivCrop.setImageBitmap(getResizedBitmap(cropImage, 800));
                        String imageInfo = "Cropped Bitmap height: " + cropImage.getHeight() + " width: " + cropImage.getWidth() +
                                " res: " + (cropImage.getHeight() * cropImage.getWidth());
                        tvCrop.setText(imageInfo);
                    }
                });

    }

    public void onLaunchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = getPhotoFileUri(intermediateName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            intermediateProvider = FileProvider.getUriForFile(TakePhotoActivity.this, FILE_PROVIDER_AUTHORITY + ".provider", photoFile);
        else
            intermediateProvider = Uri.fromFile(photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, intermediateProvider);
        if (intent.resolveActivity(getPackageManager()) != null) {
            cameraActivityResultLauncher.launch(intent);
        }
    }

    private void onCropImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            grantUriPermission("com.android.camera", intermediateProvider, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(intermediateProvider, "image/*");

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);

            int size = 0;

            if (list != null) {
                grantUriPermission(list.get(0).activityInfo.packageName, intermediateProvider, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                size = list.size();
            }

            if (size == 0) {
                Toast.makeText(this, "Error, wasn't taken image!", Toast.LENGTH_SHORT).show();
            } else {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.putExtra("crop", "true");
                intent.putExtra("scale", true);
                File photoFile = getPhotoFileUri(resultName);
                // wrap File object into a content provider
                // required for API >= 24
                // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
                resultProvider = FileProvider.getUriForFile(TakePhotoActivity.this, FILE_PROVIDER_AUTHORITY + ".provider", photoFile);
                intent.putExtra("return-data", false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, resultProvider);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

                Intent cropIntent = new Intent(intent);
                //if (rbChooseCropperApplicationFixed0.isChecked()) {
                if (useFixedCropper) {
                    ResolveInfo res = list.get(0);
                    cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    grantUriPermission(res.activityInfo.packageName, resultProvider, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    cropIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                } else {
                    // granting the rights for all registered cropping apps
                    for (int i = 0; i < list.size(); i++) {
                        ResolveInfo res = list.get(i);
                        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        grantUriPermission(res.activityInfo.packageName, resultProvider, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }
                cropActivityResultLauncher.launch(cropIntent);
            }
        } else {
            File photoFile = getPhotoFileUri(resultName);
            resultProvider = Uri.fromFile(photoFile);

            Intent intentCrop = new Intent("com.android.camera.action.CROP");
            intentCrop.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intentCrop.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentCrop.setDataAndType(intermediateProvider, "image/*");
            intentCrop.putExtra("crop", "true");
            intentCrop.putExtra("scale", true);
            intentCrop.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intentCrop.putExtra("noFaceDetection", true);
            intentCrop.putExtra("return-data", false);
            intentCrop.putExtra(MediaStore.EXTRA_OUTPUT, resultProvider);
            cropActivityResultLauncher.launch(intentCrop);
        }
    }

    private void onSaveFullImage() {
        String toastMessage;
        if ((imageUriFull == null) || (TextUtils.isEmpty(imageUriFull.toString()))) {
            toastMessage = "Please crop an image first before trying to save the result :-)";
            writeToUiToast(toastMessage);
            return;
        }
        String fileName = createImageFileName(false);
        boolean success = saveImageToExternalStorage(fileName, uriToBitmap(imageUriFull));
        if (success) {
            toastMessage = "full image " + fileName + " written to Picture folder with SUCCESS";
            writeToUiToast(toastMessage);
        } else {
            toastMessage = "fullimage " + fileName + " NOT written to Picture folder (FAILURE)";
            writeToUiToast(toastMessage);
        }
    }

    private void onSaveCroppedImage() {
        String toastMessage;
        if ((imageUriCrop == null) || (TextUtils.isEmpty(imageUriCrop.toString()))) {
            toastMessage = "Please crop an image first before trying to save the result :-)";
            writeToUiToast(toastMessage);
            return;
        }
        String fileName = createImageFileName(true);
        boolean success = saveImageToExternalStorage(fileName, uriToBitmap(imageUriCrop));
        if (success) {
            toastMessage = "cropped image " + fileName + " written to Picture folder with SUCCESS";
            writeToUiToast(toastMessage);
        } else {
            toastMessage = "cropped image " + fileName + " NOT written to Picture folder (FAILURE)";
            writeToUiToast(toastMessage);
        }
    }

    @SuppressLint("Range")
    public Bitmap rotateBitmap(Bitmap input, Uri uri) {
        Log.d(TAG, "rotateBitmap");
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cur = getContentResolver().query(uri, orientationColumn, null, null, null);
        int orientation = -1;
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
        }
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.setRotate(orientation);
        Bitmap cropped = Bitmap.createBitmap(input, 0, 0, input.getWidth(), input.getHeight(), rotationMatrix, true);
        return cropped;
    }

    /*
https://stackoverflow.com/a/38647301/8166854
You can just read the orientation of the camera sensor like indicated by Google in the documentation:
https://developer.android.com/reference/android/hardware/camera2/CameraCharacteristics.html

SENSOR_ORIENTATION

Added in API level 21
Key<Integer> SENSOR_ORIENTATION
Clockwise angle through which the output image needs to be rotated to be upright on the device screen in its native orientation.

Also defines the direction of rolling shutter readout, which is from top to bottom in the sensor's coordinate system.

Units: Degrees of clockwise rotation; always a multiple of 90

Range of valid values:
0, 90, 180, 270

This key is available on all devices.
     */
    public Bitmap rotateBitmap(Bitmap input, int orientation) {
        Log.d(TAG, "rotateBitmap");
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.setRotate(orientation);
        return Bitmap.createBitmap(input, 0, 0, input.getWidth(), input.getHeight(), rotationMatrix, true);
    }

    private int getCameraOrientation() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        int orientation = 0;
        try {
            String cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return orientation;
    }


    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // see https://developer.android.com/reference/androidx/core/content/FileProvider
        File mediaStorageDir = new File(getCacheDir(), CACHE_FOLDER);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        Log.d(TAG, "getPhotoFileUri for fileName: " + fileName + " is: " + file.getAbsolutePath());
        return file;
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private void saveBitmapFileToIntermediate(Uri sourceUri) {
        Log.d(TAG, "saveBitmapFileToIntermediate from URI: " + sourceUri);
        try {
            Bitmap bitmap = loadFromUri(sourceUri);
            File imageFile = getPhotoFileUri(intermediateName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                intermediateProvider = FileProvider.getUriForFile(TakePhotoActivity.this, FILE_PROVIDER_AUTHORITY + ".provider", imageFile);
            else
                intermediateProvider = Uri.fromFile(imageFile);

            OutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            Log.d(TAG, "intermediate file written to intermediateProvider: " + intermediateName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveBitmapFileToIntermediate(Bitmap bitmap) {
        Log.d(TAG, "saveBitmapFileToIntermediate for bitmap");
        try {
            File imageFile = getPhotoFileUri(intermediateName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                intermediateProvider = FileProvider.getUriForFile(TakePhotoActivity.this, FILE_PROVIDER_AUTHORITY + ".provider", imageFile);
            else
                intermediateProvider = Uri.fromFile(imageFile);
            OutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            Log.d(TAG, "intermediate file written to intermediateProvider: " + intermediateName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    // two different methods for Android < 10 and Android >= 10 ("Q")
    private boolean saveImageToExternalStorage(String imgName, Bitmap bmp) {
        Log.d(TAG, "saveImageToExternalStorage imgName: " + imgName);
        if (TextUtils.isEmpty(imgName)) {
            Log.d(TAG, "imgName is null or empty, aborted");
            return false;
        }
        if (bmp == null) {
            Log.d(TAG, "bmp is null, aborted");
            return false;
        }
        // https://www.youtube.com/watch?v=nA4XWsG9IPM
        Uri imageCollection = null;
        ContentResolver resolver = getContentResolver();
        // > SDK 28
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(TAG, "saveImageToExternalStorage Android version is >= 10");
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imgName + FIXED_IMAGE_EXTENSION);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri imageUri = resolver.insert(imageCollection, contentValues);
            try {
                OutputStream outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Objects.requireNonNull(outputStream);
                Log.d(TAG, "the image was stored");
                return true;
            } catch (Exception e) {
                Toast.makeText(this, "Image not saved: \n" + e, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "saveImageToExternalStorage Android version is < 10");
            // see https://stackoverflow.com/a/65141440/8166854
            //File pictureDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"My");
            File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!pictureDirectory.exists()){
                pictureDirectory.mkdir();
            }
            File bitmapFile = new File(pictureDirectory, imgName + FIXED_IMAGE_EXTENSION);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(bitmapFile);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                // update Gallery
                scanFile(bitmapFile.getAbsolutePath());
                return true;
            } catch (IOException e) {
                Toast.makeText(this, "Image not saved: \n" + e, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        return false;
    }

    // update the gallery index
    private void scanFile(String path) {
        MediaScannerConnection.scanFile(TakePhotoActivity.this,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

    // does not add the  file extension to be flexible
    private String createImageFileName(boolean isCropped) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "";
        if (isCropped) {
            fileName = timeStamp + "_cr";
        } else {
            fileName = timeStamp;
        }
        return fileName;
    }

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            Log.d(TAG, "uriToBitmap with Uri: " + selectedFileUri);
            if ((selectedFileUri == null) || (TextUtils.isEmpty(selectedFileUri.toString()))) {
                return null;
            }
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            Log.d(TAG, "return image");
            return image;
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
            e.printStackTrace();
        }
        Log.d(TAG, "return null");
        return null;
    }

    /**
     * UI handling
     */

    private void writeToUiToast(String message) {
        runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(),
                    message,
                    Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * permission handling
     */
    private void askPermissions() {
        Log.d(TAG, "askPermissions");
        //TODO ask for permission of camera upon first launch of application
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)");
            if (Build.VERSION.SDK_INT <= 32) {
                Log.d(TAG, "Build.VERSION.SDK_INT <= 32");
                //if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    Log.d(TAG, "if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED");
                    //String[] permission = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    String[] permission = {android.Manifest.permission.CAMERA};
                    requestPermissions(permission, 112);
                }
            } else {
                Log.d(TAG, "Build.VERSION.SDK_INT > 32");
                if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    Log.d(TAG, "if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED");
                    String[] permission = {Manifest.permission.CAMERA};
                    requestPermissions(permission, 112);
                }
            }
        }
    }

}