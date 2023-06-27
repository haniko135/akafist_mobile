package net.energogroup.akafist.viewmodel;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.LinksFragment;
import net.energogroup.akafist.models.LinksModel;
import net.energogroup.akafist.service.background.DownloadFromYandexTask;

import org.json.JSONException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerViewModel extends ViewModel {

    private MutableLiveData<String> workMode = new MutableLiveData<>();
    private MutableLiveData<String> urlForAudio = new MutableLiveData<>();
    private MutableLiveData<Boolean> isInitialized = new MutableLiveData<>();
    private MutableLiveData<LinksModel> linksModel = new MutableLiveData<>();
    private MutableLiveData<MediaPlayer> currMediaPlayer = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDownload = new MutableLiveData<>();
    private OneTimeWorkRequest workRequest;
    private String urlForLink, fileName, filePath;



    public MutableLiveData<String> getWorkMode() {
        return workMode;
    }

    public void setWorkMode(String workMode) {
        this.workMode.setValue(workMode);
    }

    public MutableLiveData<String> getUrlForAudio() {
        return urlForAudio;
    }

    public void setUrlForAudio(String urlForAudio) {
        this.urlForAudio.setValue(urlForAudio);
    }

    public MutableLiveData<Boolean> getIsInitialized() { return isInitialized; }

    public void setIsInitialized(Boolean isInitialized) { this.isInitialized.setValue(isInitialized); }

    public MutableLiveData<LinksModel> getLinksModel() {
        return linksModel;
    }

    public void setLinksModel(LinksModel linksModel) { this.linksModel.setValue(linksModel); }

    public MutableLiveData<MediaPlayer> getCurrMediaPlayer() {
        return currMediaPlayer;
    }

    public void setCurrMediaPlayer(MediaPlayer currMediaPlayer) {
        this.currMediaPlayer.setValue(currMediaPlayer);
    }

    public void setUrlForLink(String urlForLink) {
        this.urlForLink = urlForLink;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public MutableLiveData<Boolean> getIsDownload() {
        return isDownload;
    }

    public void setDownload(Boolean download) { isDownload.setValue(download); }

    /**
     * Этот метод запрашивает ссылку на скачивание аудиофайла через Яндекс.Диск API.
     * Есть в методе {@link LinksFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @exception JSONException
     */
    public void getLinkDownload(LayoutInflater inflater, ViewGroup container) {
        String urlToGet = "https://cloud-api.yandex.net/v1/disk/public/resources?public_key=" + urlForLink;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, //получение данных
                urlToGet, null, response -> {
            String resName, resLink;
            try {
                resName = response.getString("name");
                Log.i("YANDEX",resName);

                //имя файла
                File newFile = new File(filePath  + "/"+ fileName + ".mp3");

                //скачивание файла в фоновом режиме
                if(!newFile.exists()) {
                    resLink = response.getString("file");
                    Data data = new Data.Builder().putString("URL", resLink)
                            .putString("FILENAME", fileName + ".mp3")
                            //.putString("FILENAME", resName)
                            .putString("FILE_DIR", filePath).build();
                    workRequest = new OneTimeWorkRequest.Builder(DownloadFromYandexTask.class)
                            .setInputData(data).build();
                    WorkManager.getInstance(inflater.getContext()).enqueue(workRequest);

                    WorkManager.getInstance(inflater.getContext()).getWorkInfoByIdLiveData(workRequest.getId())
                            .observe(Objects.requireNonNull(ViewTreeLifecycleOwner.get(container.getRootView().getRootView())), workInfo -> {
                                if(workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED){
                                    String audioName = workInfo.getOutputData().getString("AUDIO_NAME");
                                    String audioLink = workInfo.getOutputData().getString("AUDIO_LINK");
                                    //downloadAudio.add(new LinksModel(audioName, audioLink));
                                    Log.i("YANDEX", "Download file: " + audioName + ", " + audioLink);
                                }else{
                                    Log.i("YANDEX", "Is not yet");
                                }
                            });

                    Log.i("YANDEX",filePath);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization: Bearer ", MainActivity.secToken);
                headers.put("User-Agent", "akafist_app_1.0.0");
                headers.put("Connection", "keep-alive");
                return headers;
            }

        };
        MainActivity.mRequestQueue.add(request);
    }

    /**
     * Этот метод создаёт уведомление о начале загрузки аудио-файла
     */
    public void preNotification(String s, Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Помощник чтеца")
                .setContentText(s)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        int NOTIFICATION_ID = 101;
        managerCompat.notify(NOTIFICATION_ID, builder.build());
    }
}
