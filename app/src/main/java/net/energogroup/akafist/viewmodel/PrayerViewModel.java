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
import net.energogroup.akafist.models.ServicesModel;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class containing data processing logic
 * {@link PrayerFragment} and {@link PrayersModels}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class PrayerViewModel extends ViewModel {

    private PrayersModels prayersModel;
    private MutableLiveData<PrayersModels> prayersModelsMutableLiveData = new MutableLiveData<>();
    private List<ServicesModel> starredPrayers = new ArrayList<>();

    /**
     * @return Current prayer
     */
    public PrayersModels getPrayersModel() {
        return prayersModel;
    }

    /**
     * @return Current array of prayers
     */
    public MutableLiveData<PrayersModels> getPrayersModelsMutableLiveData() {
        return prayersModelsMutableLiveData;
    }

    /**
     * This method sends a request to a remote server and receives a response, which later
     * used in methods {@link PrayerFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * This method is used in {@link PrayerFragment#onCreate(Bundle)}
     * @param date - Previous page type
     * @param id - Prayer id
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
