package com.example.medix.Activity.Diagnostic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import uk.co.senab.photoview.PhotoView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.medix.Common.Common;
import com.example.medix.Helper.BackgroundNotificationService;
import com.example.medix.R;
import com.squareup.picasso.Picasso;

public class ViewPrescriptionActivity extends AppCompatActivity {

    PhotoView imageViewDisplay;
    Button buttonBack;
    Button btnDownload;

    public static final String PROGRESS_UPDATE = "progress_update";
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_view_prescription);

        // getting views
        imageViewDisplay = (PhotoView) findViewById(R.id.imgDisplay);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        btnDownload = (Button) findViewById(R.id.btnDownload);

        Picasso.with(ViewPrescriptionActivity.this)
                .load(Common.PRESCRIPTION_IMAGE_URL)
                .into(imageViewDisplay);


        registerReceiver();


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*if (ActivityCompat.checkSelfPermission(ViewPrescriptionActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.PERMISSION_REQ_CODE);
                    }
                } else {

                    *//*if (!networkConfig.isNetworkAvailable()) {
                        Toast.makeText(ViewQuotesActivity.this, "You need a network connection to download this wallpaper. Please turn on mobile network or Wi-Fi in Settings.", Toast.LENGTH_SHORT).show();
                        return;
                    }*//*

                    AlertDialog alertDialog = new SpotsDialog(ViewPrescriptionActivity.this);
                    alertDialog.show();
                    alertDialog.setMessage("Please wait...");

                    String fileName = UUID.randomUUID().toString() + ".png";
                    Picasso.with(getBaseContext())
                            .load(Common.PRESCRIPTION_IMAGE_URL)
                            .into(new SaveImage(getApplicationContext(), alertDialog, getApplicationContext().getContentResolver(), fileName, "DevStack Wallpaper Image"));

                }*/

                if (checkPermission()) {
                    startImageDownload();
                } else {
                    requestPermission();
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void registerReceiver() {
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PROGRESS_UPDATE);
        bManager.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(PROGRESS_UPDATE)) {

                boolean downloadComplete = intent.getBooleanExtra("downloadComplete", false);
                //Log.d("API123", download.getProgress() + " current progress");

                if (downloadComplete) {
                    Toast.makeText(getApplicationContext(), "File download completed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void startImageDownload() {
        Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, BackgroundNotificationService.class);
        startService(intent);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startImageDownload();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
