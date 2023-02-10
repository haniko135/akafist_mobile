package com.example.akafist.viewmodel;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.akafist.MainActivity;
import com.example.akafist.R;
import com.example.akafist.models.LinksModel;
import com.example.akafist.models.ServicesModel;
import com.example.akafist.models.TypesModel;
import com.example.akafist.service.DownloadFromYandexTask;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinksViewModel extends ViewModel {
    private List<LinksModel> linksModelList = new ArrayList<>();
    private MutableLiveData<List<LinksModel>> mutableLinksDate = new MutableLiveData<>();
    private OneTimeWorkRequest workRequest;

    public MutableLiveData<List<LinksModel>> getMutableLinksDate() {
        return mutableLinksDate;
    }

    public void getJson(){
        String urlToGet = "https://pr.energogroup.org/api/church/talks";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, //получение данных
                urlToGet, null, response -> {
            JSONObject jsonObject;
            int id;
            String url, name;
            try {
                int i = 0;
                while (i < response.length()) {
                    jsonObject = response.getJSONObject(i);
                    id = jsonObject.getInt("id");
                    name = StringEscapeUtils.unescapeJava(jsonObject.getString("name"));
                    url = StringEscapeUtils.unescapeJava(jsonObject.getString("url"));
                    linksModelList.add(new LinksModel(id, url, name));
                    mutableLinksDate.setValue(linksModelList);
                    Log.e("PARSING", name);
                    i++;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "akafist_app_1.0.0");
                headers.put("Connection", "keep-alive");
                return headers;
            }

        };
        MainActivity.mRequestQueue.add(request);
    }

    public void getLinkDownload(String url, LayoutInflater inflater, ViewGroup container, String audioFilesDir) {
        String urlToGet = "https://cloud-api.yandex.net/v1/disk/public/resources?public_key=" + url;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, //получение данных
                urlToGet, null, response -> {
            String resName, resLink;
            try {
                resName = response.getString("name");
                Log.i("YANDEX",resName);

                File newFile = new File(audioFilesDir  + "/links_records/"+ resName);

                if(!newFile.exists()) {
                    resLink = response.getString("file");
                    Data data = new Data.Builder().putString("URL", resLink)
                            .putString("FILENAME", resName)
                            .putString("FILE_DIR", audioFilesDir).build();
                    workRequest = new OneTimeWorkRequest.Builder(DownloadFromYandexTask.class)
                            .setInputData(data).build();
                    WorkManager.getInstance(inflater.getContext()).enqueue(workRequest);
                    //new DownloadFromYandexTask(inflater,container).execute(resLink, resName, audioFilesDir);

                    Log.i("YANDEX",audioFilesDir);
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
}
