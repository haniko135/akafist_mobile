package net.energogroup.akafist.viewmodel;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.android.volley.Request;
import com.android.volley.Response;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.PlayerFragment;
import net.energogroup.akafist.models.LinksModel;
import net.energogroup.akafist.service.RequestServiceHandler;
import net.energogroup.akafist.service.background.DownloadFromYandexTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

/**
 * A class that provides music playback throughout the application
 * @author Nastya Izotina
 * @version 1.0.2
 */
public class PlayerViewModel extends ViewModel {

    private static final int DOWNLOAD_URL = R.string.downloadURL;
    private final MutableLiveData<String> workMode = new MutableLiveData<>();
    private final MutableLiveData<String> urlForAudio = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isInitialized = new MutableLiveData<>();
    private final MutableLiveData<LinksModel> linksModel = new MutableLiveData<>();
    private final MutableLiveData<MediaPlayer> currMediaPlayer = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDownload = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isProgressBarActive = new MutableLiveData<>();
    private OneTimeWorkRequest workRequest;
    private String urlForLink, fileName, filePath;



    public MutableLiveData<Boolean> getIsProgressBarActive() {
        return isProgressBarActive;
    }

    public void setIsProgressBarActive(Boolean isProgressBarActive) {
        this.isProgressBarActive.setValue(isProgressBarActive);
    }

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

    public void setIsInitialized(Boolean isInitialized) {
        this.isInitialized.setValue(isInitialized);
    }

    public MutableLiveData<LinksModel> getLinksModel() {
        return linksModel;
    }

    public void setLinksModel(LinksModel linksModel) {
        this.linksModel.setValue(linksModel);
    }

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

    public void setDownload(Boolean download) {
        isDownload.setValue(download);
    }

    /**
     * This method requests a link to download an audio file from Yandex.Disk API.
     * It is using in method {@link PlayerFragment#initButtonClicks(ViewGroup)}
     * @param container ViewGroup
     */
    public void getLinkDownload(LayoutInflater inflater, ViewGroup container) {
        String urlToGet = inflater.getContext().getResources().getString(DOWNLOAD_URL) + urlForLink;

        RequestServiceHandler serviceHandler = new RequestServiceHandler();
        serviceHandler.addHeader("Authorization: Bearer ", inflater.getContext().getResources().getString(MainActivity.SEC_TOKEN));
        serviceHandler.addHeader("User-Agent", inflater.getContext().getResources().getString(MainActivity.APP_VER));
        serviceHandler.addHeader("Connection", "keep-alive");

        serviceHandler.objectRequest(urlToGet, Request.Method.GET,
                null, JSONObject.class,
                (Response.Listener<JSONObject>) response -> {
                    String resName, resLink;
                    try {
                        response.getString("name");

                        //имя файла
                        File newFile = new File(filePath + "/" + fileName + ".mp3");

                        //скачивание файла в фоновом режиме
                        if (!newFile.exists()) {
                            resLink = response.getString("file");
                            Data data = new Data.Builder().putString("URL", resLink)
                                    .putString("FILENAME", fileName + ".mp3")
                                    .putString("FILE_DIR", filePath).build();
                            workRequest = new OneTimeWorkRequest.Builder(DownloadFromYandexTask.class)
                                    .setInputData(data).build();
                            WorkManager.getInstance(inflater.getContext()).enqueue(workRequest);

                            WorkManager.getInstance(inflater.getContext()).getWorkInfoByIdLiveData(workRequest.getId())
                                    .observe(Objects.requireNonNull(ViewTreeLifecycleOwner.get(container.getRootView().getRootView())), workInfo -> {
                                        if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                                            String audioName = workInfo.getOutputData().getString("AUDIO_NAME");
                                            String audioLink = workInfo.getOutputData().getString("AUDIO_LINK");
                                            //downloadAudio.add(new LinksModel(audioName, audioLink));
                                            Log.i("YANDEX", "Download file: " + audioName + ", " + audioLink);
                                        } else {
                                            Log.i("YANDEX", "Is not yet");
                                        }
                                    });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("Response", error.getMessage()));
    }
}
