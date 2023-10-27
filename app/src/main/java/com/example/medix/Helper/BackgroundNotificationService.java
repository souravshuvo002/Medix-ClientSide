package com.example.medix.Helper;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import com.example.medix.Activity.Diagnostic.ViewPrescriptionActivity;
import com.example.medix.Api.ApiService;
import com.example.medix.Api.ApiURL;
import com.example.medix.Common.Common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class BackgroundNotificationService extends IntentService {

    public BackgroundNotificationService() {
        super("Service");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private String fileName;


    @Override
    protected void onHandleIntent(Intent intent) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("id", "an", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("no sound");
            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent intent2 = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent2, 0);

        notificationBuilder = new NotificationCompat.Builder(this, "id")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Download")
                .setContentText("Downloading Image")
                .setDefaults(0)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());

        initRetrofit();

    }

    private void initRetrofit() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiURL.MEDIX_URL)
                .build();

        ApiService retrofitInterface = retrofit.create(ApiService.class);

        Call<ResponseBody> request = retrofitInterface.downloadImage(Common.PRESCRIPTION_IMAGE_URL);
        try {

            downloadImage(request.execute().body());

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void downloadImage(ResponseBody body) throws IOException {

        fileName = UUID.randomUUID().toString() + ".png";

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream inputStream = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        OutputStream outputStream = new FileOutputStream(outputFile);
        long total = 0;
        boolean downloadComplete = false;
        //int totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));

        while ((count = inputStream.read(data)) != -1) {

            total += count;
            int progress = (int) ((double) (total * 100) / (double) fileSize);


            updateNotification(progress);
            outputStream.write(data, 0, count);
            downloadComplete = true;
        }
        onDownloadComplete(downloadComplete);
        outputStream.flush();
        outputStream.close();
        inputStream.close();

    }

    private void updateNotification(int currentProgress) {
        notificationBuilder.setProgress(100, currentProgress, false);
        notificationBuilder.setContentText("Downloaded: " + currentProgress + "%");
        notificationManager.notify(0, notificationBuilder.build());
    }


    private void sendProgressUpdate(boolean downloadComplete) {

        Intent intent = new Intent(ViewPrescriptionActivity.PROGRESS_UPDATE);
        intent.putExtra("downloadComplete", downloadComplete);
        LocalBroadcastManager.getInstance(BackgroundNotificationService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete(boolean downloadComplete) {
        sendProgressUpdate(downloadComplete);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("Image Download Complete");
        notificationManager.notify(0, notificationBuilder.build());

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}