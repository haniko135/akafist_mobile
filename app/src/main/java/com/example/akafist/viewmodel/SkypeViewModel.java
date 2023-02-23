package com.example.akafist.viewmodel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.akafist.MainActivity;
import com.example.akafist.fragments.SkypesFragment;
import com.example.akafist.fragments.SkypesBlocksFragment;
import com.example.akafist.models.SkypesConfs;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс, содержащий логику обработки данных
 * {@link SkypesBlocksFragment} и {@link SkypesFragment}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class SkypeViewModel extends ViewModel {
    private List<SkypesConfs> skypeModels = new ArrayList<>();
    private List<SkypesConfs> confsModels = new ArrayList<>();
    private MutableLiveData<List<SkypesConfs>> skypesMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<SkypesConfs>> confsMutableLiveData = new MutableLiveData<>();

    /**
     * @return Возвращает текущие группы конференций
     */
    public MutableLiveData<List<SkypesConfs>> getSkypesMutableLiveData() {
        return skypesMutableLiveData;
    }

    /**
     * @return Возвращает текущие конференции
     */
    public MutableLiveData<List<SkypesConfs>> getConfsMutableLiveData() {
        return confsMutableLiveData;
    }

    /**
     * Этот метод отправляет запрос на удалённый сервер и получает ответ, который в последствии
     * используется в методе {@link SkypesFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * Данный метод используется в {@link SkypesFragment#onCreate(Bundle)}
     * @exception JSONException
     */
    public void getJsonSkype(){
        String urlToGet2 = "https://pr.energogroup.org/api/church/skype";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, //получение данных
                urlToGet2, null, response -> {
            JSONArray confs, blocks;
            JSONObject jsonObject;
            int id;
            String  name, url;
            try {
                confs = response.getJSONArray("confs");
                int i = 0;
                while (i < confs.length()) {
                    jsonObject = confs.getJSONObject(i);
                    id = jsonObject.getInt("id");
                    name = StringEscapeUtils.unescapeJava(jsonObject.getString("name"));
                    url = StringEscapeUtils.unescapeJava(jsonObject.getString("url"));
                    confsModels.add(new SkypesConfs(id, name, url));
                    confsMutableLiveData.setValue(confsModels);
                    Log.e("PARSING", name);
                    i++;
                }
                i=0;
                blocks = response.getJSONArray("blocks");
                while (i < (blocks).length()) {
                    jsonObject = blocks.getJSONObject(i);
                    id = jsonObject.getInt("id");
                    name = StringEscapeUtils.unescapeJava(jsonObject.getString("name"));
                    skypeModels.add(new SkypesConfs(id, name));
                    skypesMutableLiveData.setValue(skypeModels);
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

    /**
     * Этот метод отправляет запрос на удалённый сервер и получает ответ, который в последствии
     * используется в методе {@link SkypesBlocksFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * Данный метод используется в {@link SkypesBlocksFragment#onCreate(Bundle)}
     * @param urlId ID конференции
     * @exception JSONException
     */
    public void getJsonSkypeBlock(int urlId){
        String urlToGet = "https://pr.energogroup.org/api/church/skype/"+urlId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, //получение данных
                urlToGet, null, response -> {
            JSONArray confs;
            JSONObject jsonObject;
            int id;
            String  name, url;
            try {
                confs = response.getJSONArray("confs");
                int i = 0;
                while (i <= confs.length()) {
                    jsonObject = confs.getJSONObject(i);
                    id = jsonObject.getInt("id");
                    name = StringEscapeUtils.unescapeJava(jsonObject.getString("name"));
                    url = StringEscapeUtils.unescapeJava(jsonObject.getString("url"));
                    confsModels.add(new SkypesConfs(id, name, url));
                    confsMutableLiveData.setValue(confsModels);
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
}
