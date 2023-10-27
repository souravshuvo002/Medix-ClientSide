package com.example.medix.Activity.Diagnostic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.medix.Api.ApiService;
import com.example.medix.Api.ApiURL;
import com.example.medix.Helper.CheckPermission;
import com.example.medix.Model.Result;
import com.example.medix.R;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadPrescriptionActivity extends AppCompatActivity {

    private ImageView imageViewPrescription, imageViewGallery, imageViewCam;
    private LinearLayout linearLayShowImage;
    private Button buttonUpload, buttonRemove;
    private TextInputEditText editTextName, editTextPhone, editTextMessage;

    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String IMAGE_DIRECTORY = "Medix";
    private static final int PICK_CAMERA_IMAGE = 2;
    private static final int PICK_GALLERY_IMAGE = 1;
    private File file;
    private File sourceFile;
    private File compressedImageFile;
    private SimpleDateFormat dateFormatter;
    private Uri imageCaptureUri;
    private Uri resultUri;
    private Uri imageUri;
    private Bitmap bp;
    private byte[] pic1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_prescription);

        // Change status bar color
        changeStatusBarColor("#00574B");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Upload Prescription");

        // getting views
        linearLayShowImage = (LinearLayout) findViewById(R.id.linearLayShowImage);
        editTextName = (TextInputEditText) findViewById(R.id.editTextName);
        editTextPhone = (TextInputEditText) findViewById(R.id.editTextPhone);
        editTextMessage = (TextInputEditText) findViewById(R.id.editTextMessage);
        imageViewPrescription = (ImageView) findViewById(R.id.imageViewPrescription);
        imageViewCam = (ImageView) findViewById(R.id.imageViewCam);
        imageViewGallery = (ImageView) findViewById(R.id.imageViewGallery);

        buttonUpload = (Button) findViewById(R.id.btnUpload);
        buttonRemove = (Button) findViewById(R.id.buttonRemove);

        // for Camera Error fix
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // make directory for temp image
        file = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }

        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);

        imageViewCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImageFromCamera();
            }
        });

        imageViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearLayShowImage.setVisibility(View.GONE);
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextName.getText().toString()) ||
                        TextUtils.isEmpty(editTextPhone.getText().toString()) ||
                        TextUtils.isEmpty(editTextMessage.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Some of fields is empty", Toast.LENGTH_LONG).show();
                } else if (compressedImageFile == null || linearLayShowImage.getVisibility() == View.GONE) {
                    Toast.makeText(getApplicationContext(), "Upload image can't be blank", Toast.LENGTH_LONG).show();
                } else {
                    uploadPrescription();
                }
            }
        });

    }

    private void uploadPrescription() {
        String CUSTOMER_ID;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(UploadPrescriptionActivity.this);
        CUSTOMER_ID = sharedPreferences.getString("CUSTOMER_ID", null);

        RequestBody customer_id = RequestBody.create(MediaType.parse("text/plain"), CUSTOMER_ID);
        RequestBody customer_name = RequestBody.create(MediaType.parse("text/plain"), editTextName.getText().toString());
        RequestBody customer_phone = RequestBody.create(MediaType.parse("text/plain"), editTextPhone.getText().toString());
        RequestBody note = RequestBody.create(MediaType.parse("text/plain"), editTextMessage.getText().toString());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), compressedImageFile);

        MultipartBody.Part body = MultipartBody.Part.createFormData("image", compressedImageFile.getName(), requestFile);

        /**
         * Uploading Task to te perform here
         *
         * Database Table Field (name, link(image))
         *
         */
        final android.app.AlertDialog waitingDialog = new SpotsDialog(UploadPrescriptionActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiURL.MEDIX_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        ApiService service = retrofit.create(ApiService.class);
        //defining the call
        Call<Result> call = service.uploadPrescription(customer_id, customer_name, customer_phone, note, body);

        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                editTextName.setText(null);
                editTextPhone.setText(null);
                editTextMessage.setText(null);
                linearLayShowImage.setVisibility(View.GONE);
                //imageViewPrescription.setImageResource(R.drawable.add_image_bg);
                waitingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e("Message: ", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                waitingDialog.dismiss();
            }
        });
    }

    // Image take from Gallery
    private void selectImageFromGallery() {
        CheckPermission checkPermission = new CheckPermission(UploadPrescriptionActivity.this);
        if (checkPermission.checkPermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_GALLERY_IMAGE);
        } else {
            checkPermission.requestPermission();
        }
    }

    // Image take from camera
    private void captureImageFromCamera() {
        CheckPermission checkPermission = new CheckPermission(UploadPrescriptionActivity.this);
        if (checkPermission.checkPermission()) {
            sourceFile = new File(file, "img_"
                    + dateFormatter.format(new Date()).toString() + ".png");
            imageCaptureUri = Uri.fromFile(sourceFile);

            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
            startActivityForResult(intentCamera, PICK_CAMERA_IMAGE);
        } else {
            checkPermission.requestPermission();
        }
    }

    // remove image
    private void removeImage() {
        imageViewPrescription.setImageResource(R.drawable.add_image_bg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case PICK_GALLERY_IMAGE:
                    Uri imageUri = data.getData();
                    //imageView.setImageURI(imageUri);
                    linearLayShowImage.setVisibility(View.VISIBLE);
                    imageViewPrescription.setVisibility(View.VISIBLE);
                    Picasso.with(this).load(imageUri).into(imageViewPrescription);
                    //filePath = getRealPathFromURI(imageUri);
                    sourceFile = new File(getRealPathFromURI(imageUri));
                    compressImageWithZetbaitsuLibrary(sourceFile);       // Here i'm compress the image

                    break;
                case PICK_CAMERA_IMAGE:
                    if (imageCaptureUri == null) {
                        Toast.makeText(getApplicationContext(), "Uri empty", Toast.LENGTH_LONG).show();
                    } else {
                        //imageView.setImageURI(imageCaptureUri);
                        linearLayShowImage.setVisibility(View.VISIBLE);
                        imageViewPrescription.setVisibility(View.VISIBLE);
                        Picasso.with(this).load(imageCaptureUri).into(imageViewPrescription);
                        compressImageWithZetbaitsuLibrary(sourceFile);
                    }

                    break;
            }
        }
    }

    private void compressImageWithZetbaitsuLibrary(File sourceFile) {
        new Compressor(this)
                .setMaxWidth(1024)
                .setMaxHeight(1024)
                .setQuality(80)
                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES + "/" + IMAGE_DIRECTORY).getAbsolutePath())
                .compressToFileAsFlowable(sourceFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        compressedImageFile = file;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    private void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
