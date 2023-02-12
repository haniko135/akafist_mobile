package com.example.akafist.service;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.akafist.MainActivity;
import com.example.akafist.R;
import com.example.akafist.databinding.FragmentLinksBinding;
import com.example.akafist.fragments.LinksFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.internal.Util;

public class DownloadFromYandexTask extends Worker {

    public File outFile;
    private Context context;
    private long downloadID;

    private final String tag = "FILES_AND_STORAGE";
    private final int NOTIFICATION_ID = 101;

    public DownloadFromYandexTask(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        ListenableWorker.Result result = Result.failure();
        try {
            URL urlDownload = new URL(getInputData().getString("URL"));
            HttpsURLConnection downConn = (HttpsURLConnection) urlDownload.openConnection();
            downConn.setRequestMethod("GET");
            downConn.setRequestProperty("Authorization: OAuth ", MainActivity.secToken);
            downConn.setRequestProperty("User-Agent","akafist_app/1.0.0");
            downConn.setRequestProperty("Connection", "keep-alive");
            downConn.setConnectTimeout(5000);
            //downConn.setReadTimeout(1000);
            downConn.connect();

            if(downConn.getResponseCode() == 200){

                File androidStorage;

                androidStorage = new File( getInputData().getString("FILE_DIR") + "/links_records");
                Log.i(tag, androidStorage.getPath());
                if(!androidStorage.exists()){
                    androidStorage.mkdir();
                    Log.i(tag, "Directory created");
                }

                String downloadName = getInputData().getString("FILENAME");
                outFile = new File(androidStorage, downloadName);
                Log.i("FILES_AND_STORAGE", outFile.getPath());
                if(!outFile.exists()){
                    outFile.createNewFile();
                    Log.i(tag, "File created");
                }

                FileOutputStream fos = new FileOutputStream(outFile);
                InputStream is = downConn.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }

                fos.close();
                is.close();
                downConn.disconnect();

                Log.i(tag, "Download complete");

                Data data = new Data.Builder().putString("AUDIO_NAME", downloadName)
                        .putString("AUDIO_LINK", outFile.getPath()).build();

                result = Worker.Result.success(data);
            } else if (downConn.getResponseCode() == 403) {
                Log.i(tag, "Token is invalid");
                result = ListenableWorker.Result.failure();
            } else if (downConn.getResponseCode() == 404){
                Log.i(tag, "Resource not found");
                result = ListenableWorker.Result.failure();
            } else if(downConn.getResponseCode() == 406){
                Log.i(tag, "Invalid response format");
                result = ListenableWorker.Result.failure();
            } else if(downConn.getResponseCode() == 413) {
                Log.i(tag, "Too big file. Can't download");
                result = ListenableWorker.Result.failure();
            } else if (downConn.getResponseCode() == 503){
                Log.i(tag, "Server's error");
                result = ListenableWorker.Result.failure();
            }

            /*String downloadName = strings[1].toLowerCase(Locale.ROOT).replace(" ", "_");
            File file = new File(strings[2]+"/links_records/" + downloadName);
            Uri uri = Uri.fromFile(file);
            Log.e("YANDEX", uri.toString());

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(strings[0]))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    //.setDestinationInExternalFilesDir(binding.getRoot().getContext(), Environment.DIRECTORY_MUSIC, "/links_records/"+downloadName.toLowerCase(Locale.ROOT))
                    .setDestinationUri(Uri.parse(Environment.DIRECTORY_DOWNLOADS))
                    .setTitle(downloadName)
                    .setDescription("Файл качается")
                    .setAllowedOverMetered(true)
                    //.addRequestHeader("Authorization", "OAuth" + LinksFragment.secToken)
                    .addRequestHeader("User-Agent","akafist_app/1.0.0")
                    .addRequestHeader("Connection", "keep-alive")
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setMimeType("audio/mpeg");

            Log.e("YANDEX", strings[2]+"/links_records/"+downloadName.toLowerCase(Locale.ROOT).replace(" ", "_"));

            DownloadManager downloadManager = (DownloadManager) binding.getRoot().getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            downloadID = downloadManager.enqueue(request);*/

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(tag, "Download Error Exception " + e.getMessage());
            result = ListenableWorker.Result.failure();
        }
        postNotification();
        return result;
    }


    /*private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(binding.getRoot().getContext(), "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }
    };*/

    private void postNotification() {
        //binding.getRoot().getContext().registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        try {
            if (outFile == null) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_download_24)
                        .setContentTitle("Помощник чтеца")
                        .setContentText("Ошибка при скачивании")
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                managerCompat.notify(NOTIFICATION_ID, builder.build());

                new Handler().postDelayed(() -> {
                    NotificationCompat.Builder builder1 = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_baseline_download_24)
                            .setContentTitle("Помощник чтеца")
                            .setContentText("Попробуйте скачать заново")
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat managerCompat1 = NotificationManagerCompat.from(context);
                    managerCompat1.notify(NOTIFICATION_ID, builder1.build());

                    Log.i(tag,"Download Again");
                }, 2000);

                Log.e(tag, "Download Failed");

            }
            else{
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_download_24)
                        .setContentTitle("Помощник чтеца")
                        .setContentText("Файл скачан")
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                managerCompat.notify(NOTIFICATION_ID, builder.build());

                Log.i(tag, "Download Success");
            }
        } catch (Exception e) {
            e.printStackTrace();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_download_24)
                    .setContentTitle("Помощник чтеца")
                    .setContentText("Ошибка при скачивании")
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
            managerCompat.notify(NOTIFICATION_ID, builder.build());

            new Handler().postDelayed(() -> {
                NotificationCompat.Builder builder12 = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_download_24)
                        .setContentTitle("Помощник чтеца")
                        .setContentText("Попробуйте скачать заново")
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat managerCompat12 = NotificationManagerCompat.from(context);
                managerCompat12.notify(NOTIFICATION_ID, builder12.build());
            }, 3000);
            Log.e(tag, "Download Failed with Exception - " + e.getLocalizedMessage());

        }
    }

}
