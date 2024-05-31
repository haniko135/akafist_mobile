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
import net.energogroup.akafist.fragments.ChurchFragment;
import net.energogroup.akafist.models.ServicesModel;
import net.energogroup.akafist.models.TypesModel;
import net.energogroup.akafist.service.RequestServiceHandler;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A class containing data processing logic of
 * {@link ChurchFragment}, {@link TypesModel} and {@link ServicesModel}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class ChurchViewModel extends ViewModel{
    private final List<TypesModel> typesModelList = new ArrayList<>();
    private final MutableLiveData<List<TypesModel>> mutableTypesList = new MutableLiveData<>();
    private final List<ServicesModel> servicesModelList = new ArrayList<>();
    private final MutableLiveData<List<ServicesModel>> mutableServicesList = new MutableLiveData<>();
    private final MutableLiveData<Integer> curId = new MutableLiveData<>();
    private String dateTxt, nameTxt;
    private final MutableLiveData <String> liveDataTxt = new MutableLiveData<>();
    private final MutableLiveData <String> liveNameTxt = new MutableLiveData<>();

    /**
     * @param id Current type id
     */
    public void setCurId(int id){
        curId.setValue(id);
    }

    /**
     * @return Current list of types
     */
    public MutableLiveData<List<TypesModel>> getMutableTypesList() {
        return mutableTypesList;
    }

    /**
     * @return Current type id
     */
    public MutableLiveData<Integer> getCurId() {
        return curId;
    }

    /**
     * @return Current Prayer List
     */
    public MutableLiveData<List<ServicesModel>> getMutableServicesList() {
        return mutableServicesList;
    }

    /**
     * @return Current upper block name
     */
    public MutableLiveData<String> getLiveDataTxt() {
        return liveDataTxt;
    }

    /**
     * @return Current lower block name
     */
    public MutableLiveData<String> getLiveNameTxt() {
        return liveNameTxt;
    }

    /**
     * This method sends a request to a remote server and receives a response,
     * which is subsequently used in the method {@link ChurchFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * This method is used in {@link ChurchFragment#onCreate(Bundle)}
     * @param date Page type
     * @exception JSONException
     */
    public void getJson(String date, Context context){
        String urlToGet = context.getResources().getString(MainActivity.API_PATH)+date;

        RequestServiceHandler serviceHandler = new RequestServiceHandler();
        serviceHandler.addHeader("User-Agent", context.getResources().getString(MainActivity.APP_VER));
        serviceHandler.addHeader("Connection", "keep-alive");

        serviceHandler.objectRequest(urlToGet, Request.Method.GET,
                null, JSONObject.class,
                (Response.Listener<JSONObject>) response -> {
                    JSONArray types, services;
                    JSONObject jsonObject;
                    int id, type;
                    String  name;
                    try {
                        dateTxt = response.getString("dateTxt");
                        nameTxt = response.getString("name");
                        liveDataTxt.setValue(dateTxt);
                        liveNameTxt.setValue(nameTxt);
                        types = response.getJSONArray("types");
                        int i = 0;
                        while (i < types.length()) {
                            jsonObject = types.getJSONObject(i);
                            id = jsonObject.getInt("id");
                            name = StringEscapeUtils.unescapeJava(jsonObject.getString("name"));
                            typesModelList.add(new TypesModel(id, name));
                            mutableTypesList.setValue(typesModelList);
                            i++;
                        }
                        i=0;
                        services = response.getJSONArray("services");
                        while (i < services.length()) {
                            jsonObject = services.getJSONObject(i);
                            id = jsonObject.getInt("id");
                            name = StringEscapeUtils.unescapeJava(jsonObject.getString("name"));
                            type = jsonObject.getInt("type");
                            servicesModelList.add(new ServicesModel(id, name, type, date));
                            mutableServicesList.setValue(servicesModelList);
                            i++;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("Response", error.getMessage()));
    }
}
