package net.energogroup.akafist.viewmodel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.fragments.PrayerFragment;
import net.energogroup.akafist.models.PrayersModels;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс, содержащий логику обработки данных
 * {@link PrayerFragment} и {@link PrayersModels}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class PrayerViewModel extends ViewModel {

    private PrayersModels prayersModel;
    private MutableLiveData<PrayersModels> prayersModelsMutableLiveData = new MutableLiveData<>();

    /**
     * @return Возвращает текущую молитву
     */
    public PrayersModels getPrayersModel() {
        return prayersModel;
    }

    /**
     * @return Возвращает текущий массив молитв
     */
    public MutableLiveData<PrayersModels> getPrayersModelsMutableLiveData() {
        return prayersModelsMutableLiveData;
    }

    /**
     * Этот метод отправляет запрос на удалённый сервер и получает ответ, который в последствии
     * используется в методах {@link PrayerFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * Данный метод используется в {@link PrayerFragment#onCreate(Bundle)}
     * @param date - тип предыдущей страницы
     * @param id - id молитвы
     * @exception JSONException
     */
    public void getJson(String date, int id, Context context){
        String urlToGet = context.getString(MainActivity.API_PATH)+date+"/"+id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                urlToGet, null, response -> {
            int prev, next;
            String  name, html;
            try {
                name = StringEscapeUtils.unescapeJava(response.getString("name"));
                html = StringEscapeUtils.unescapeJava(response.getString("html"));
                prev = response.getInt("prev");
                next = response.getInt("next");
                prayersModel = new PrayersModels(name,html,prev, next);
                prayersModelsMutableLiveData.setValue(prayersModel);
                Log.e("PARSING", name);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", context.getString(MainActivity.APP_VER));
                headers.put("Connection", "keep-alive");
                return headers;
            }

        };
        MainActivity.mRequestQueue.add(request);
    }
}
