package net.energogroup.akafist.service.background;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * This class downloads audio from Yandex.Disk
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class DownloadFromYandexTask extends Worker {

    public File outFile;
    private final Context context;
    private final String tag = "FILES_AND_STORAGE";

    private static final int successStringId = R.string.sucessDownload;
    private static final int failStringId = R.string.failDownload;
    private static final int againStringId = R.string.againDownload;
    private static final int appVersion = R.string.app_ver;

    /**
     * Constructor of a class, extended from {@link Worker}
     * @param context Context
     * @param workerParams WorkerParams
     */
    public DownloadFromYandexTask(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    /**
     * This method downloads audio in background thread
     * @return Result
     */
    @NonNull
    @Override
    public Result doWork() {
        ListenableWorker.Result result = Result.failure();
        try {
            URL urlDownload = new URL(getInputData().getString("URL"));
            HttpsURLConnection downConn = (HttpsURLConnection) urlDownload.openConnection();
            downConn.setRequestMethod("GET");
            downConn.setRequestProperty("Authorization: OAuth ", context.getResources().getString(MainActivity.SEC_TOKEN));
            downConn.setRequestProperty("User-Agent",context.getResources().getString(appVersion));
            downConn.setRequestProperty("Connection", "keep-alive");
            downConn.setConnectTimeout(5000);
            //downConn.setReadTimeout(1000);
            downConn.connect();

            if(downConn.getResponseCode() == 200){

                File androidStorage;

                androidStorage = new File(getInputData().getString("FILE_DIR"));
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
                int len1;//init length
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

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(tag, "Download Error Exception " + e.getMessage());
            result = ListenableWorker.Result.failure();
        }
        postNotification();
        return result;
    }

    /**
     * This method publishes a notification at the end of the work of a method {@link DownloadFromYandexTask#doWork()}
     */
    private void postNotification() {
        try {
            if (outFile == null) {
                MainActivity.generateNotification(failStringId, context);
                new Handler().postDelayed(() -> {
                    MainActivity.generateNotification(againStringId, context);
                    Log.i(tag,"Download Again");
                }, 2000);
                Log.e(tag, "Download Failed");
            }
            else{
                MainActivity.generateNotification(successStringId,context);
                Log.i(tag, "Download Success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.generateNotification(failStringId, context);

            new Handler().postDelayed(() -> MainActivity.generateNotification(againStringId, context), 3000);
            Log.e(tag, "Download Failed with Exception - " + e.getLocalizedMessage());
        }
    }

}
