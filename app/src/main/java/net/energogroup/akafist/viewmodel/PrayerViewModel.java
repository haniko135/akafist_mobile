package net.energogroup.akafist.viewmodel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.Response;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.fragments.PrayerFragment;
import net.energogroup.akafist.models.PrayersModels;
import net.energogroup.akafist.service.RequestServiceHandler;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class containing data processing logic
 * {@link PrayerFragment} and {@link PrayersModels}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class PrayerViewModel extends ViewModel {

    private PrayersModels prayersModel;
    private MutableLiveData<PrayersModels> prayersModelsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isRendered = new MutableLiveData<>(false);

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

    public void setPrayersModelsMutableLiveData(MutableLiveData<PrayersModels> prayersModels) {
        this.prayersModelsMutableLiveData = prayersModels;
    }

    public MutableLiveData<Boolean> getIsRendered() { return isRendered; }

    /**
     * This method sends a request to a remote server and receives a response, which later
     * used in methods {@link PrayerFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * This method is used in {@link PrayerFragment#onCreate(Bundle)}
     * @param date - Previous page type
     * @param id - Prayer id
     */
    public void getJson(String date, int id, Context context){
        isRendered.setValue(false);
        String urlToGet = context.getString(MainActivity.API_PATH)+date+"/"+id;

        RequestServiceHandler serviceHandler = new RequestServiceHandler();
        serviceHandler.addHeader("User-Agent", context.getString(MainActivity.APP_VER));
        serviceHandler.addHeader("Connection", "keep-alive");

        serviceHandler.objectRequest(urlToGet, Request.Method.GET,
                null, JSONObject.class,
                (Response.Listener<JSONObject>) response -> {
                    int prev, next;
                    String  name, html;
                    try {
                        name = StringEscapeUtils.unescapeJava(response.getString("name"));
                        html = StringEscapeUtils.unescapeJava(response.getString("html"));
                        prev = response.getInt("prev");
                        next = response.getInt("next");
                        prayersModel = new PrayersModels(name,html,prev, next);
                        prayersModelsMutableLiveData.setValue(prayersModel);
                        isRendered.setValue(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("Response", error.getMessage()));
    }
}
