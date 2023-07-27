package net.energogroup.akafist.viewmodel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.LinksFragment;
import net.energogroup.akafist.models.LinksModel;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс, содержащий логику обработки данных
 * {@link LinksFragment} и {@link LinksModel}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class LinksViewModel extends ViewModel {
    private List<LinksModel> linksModelList = new ArrayList<>();
    private MutableLiveData<List<LinksModel>> mutableLinksDate = new MutableLiveData<>();
    private List<LinksModel> downloadAudio = new ArrayList<>();
    private int image;
    private static final int appVersion = R.string.app_ver;

    /**
     * Этот метод возвращает текущее значение MutableLiveData<List<LinksModel>>
     * @return MutableLiveData<List<LinksModel>>
     */
    public MutableLiveData<List<LinksModel>> getMutableLinksDate() {
        return mutableLinksDate;
    }

    /**
     * Этот метод делает запрос к удалённому серверу в зависимости от
     * параметра cas и получает данные для вывода
     * в методе {@link LinksFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * @param cas String
     * @param inflater LayoutInflater
     * @exception JSONException
     */
    public void getJson(String cas, LayoutInflater inflater){
        if (cas.equals("links")) {
            String urlToGet = "https://pr.energogroup.org/api/church/talks";

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, //получение данных
                    urlToGet, null, response -> {
                JSONObject jsonObject;
                image = R.mipmap.ic_launcher;
                int id;
                String url, name;
                try {
                    int i = 0;
                    while (i < response.length()) {
                        jsonObject = response.getJSONObject(i);
                        id = jsonObject.getInt("id");
                        name = StringEscapeUtils.unescapeJava(jsonObject.getString("name"));
                        url = StringEscapeUtils.unescapeJava(jsonObject.getString("url"));
                        linksModelList.add(new LinksModel(id, url, name, image));
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
                    headers.put("User-Agent", inflater.getContext().getResources().getString(appVersion));
                    headers.put("Connection", "keep-alive");
                    return headers;
                }

            };
            MainActivity.mRequestQueue.add(request);
        }
        else if(cas.equals("molitvyOfflain")){
            image = R.mipmap.ic_launcher;
            List<String> molitvyName = Arrays.asList(inflater.getContext().getResources().getStringArray(R.array.molitvy_offline_audio_name));
            List<String> molitvyLink = Arrays.asList(inflater.getContext().getResources().getStringArray(R.array.molitvy_offline_audio_link));
            for (int i=0; i<molitvyName.size(); i++){
                linksModelList.add(new LinksModel(molitvyLink.get(i), molitvyName.get(i), image));
            }
            mutableLinksDate.setValue(linksModelList);
        }
    }

    /**
     * Этот метод используется при обновлении страницы
     * в {@link LinksFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * @param cas String
     * @param inflater LayoutInflater
     */
    public void retryGetJson(String cas, LayoutInflater inflater){
        if (cas.equals("links")) {
            linksModelList = new ArrayList<>();
            getJson(cas, inflater);
        }else if(cas.equals("molitvyOfflain")){
            linksModelList = new ArrayList<>();
            getJson(cas, inflater);
        }
    }



    /**
     * Этот метод формирует список скачанных аудио-файлов
     * @param audioFilesDir String
     * @return List<LinksModel>
     */
    @SuppressLint("SuspiciousIndentation")
    public List<LinksModel> getDownload(String audioFilesDir){
        downloadAudio.clear();
        String fullPath = audioFilesDir+"/";
        File directory = new File(fullPath);
        File[] files = directory.listFiles();
        if (files != null && files.length > 0)
        for (File file : files) {
            String filename = file.getName().substring(0,file.getName().length()-4);
            downloadAudio.add(new LinksModel(fullPath + file.getName(), filename));
        }
        return downloadAudio;
    }
}
