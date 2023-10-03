package net.energogroup.akafist.viewmodel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.Home;
import net.energogroup.akafist.fragments.Menu;
import net.energogroup.akafist.models.HomeBlocksModel;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class containing data processing logic
 * {@link Menu}, {@link Home} and {@link HomeBlocksModel}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class MenuViewModel extends ViewModel {
    private List<HomeBlocksModel> blocksModelList = new ArrayList<>();
    private MutableLiveData<List<HomeBlocksModel>> mutableLiveData = new MutableLiveData<>();

    public List<HomeBlocksModel> getBlocksModelList() {
        return blocksModelList;
    }

    public MutableLiveData<List<HomeBlocksModel>> getMutableLiveData() {
        return mutableLiveData;
    }

    /**
     * This method initializes the list of blocks of the "Home" and "Menu" pages for the first time
     */
    public void firstSet(String mode, Context context){
        if (mode.equals("energogroup")) {
            blocksModelList.add(new HomeBlocksModel("skypeConfs",
                    context.getString(R.string.skype_confs), context.getString(R.string.skype_confs_2)));
        }
        blocksModelList.add(new HomeBlocksModel("onlineMichael",
                context.getString(R.string.online_Michael), context.getString(R.string.online_Michael_2)));
        blocksModelList.add(new HomeBlocksModel("onlineVarvara",
                context.getString(R.string.online_Varvara), context.getString(R.string.online_Varvara_2)));
        blocksModelList.add(new HomeBlocksModel("molitvyOfflain",
                context.getString(R.string.molitvy_offline), context.getString(R.string.molitvy_offline_2)));
        blocksModelList.add(new HomeBlocksModel("links",
                context.getString(R.string.links1), context.getString(R.string.links2)));
        blocksModelList.add(new HomeBlocksModel("notes",
                context.getString(R.string.notes1), context.getString(R.string.notes2)));
        blocksModelList.add(new HomeBlocksModel("talks",
                context.getString(R.string.talks1), context.getString(R.string.talks2)));
        mutableLiveData.setValue(blocksModelList);
    }

    /**
     * This method sends a request to a remote server and receives a response,
     * which is later used in the methods {@link Home#onCreateView(LayoutInflater, ViewGroup, Bundle)} and
     * {@link Menu#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * This method is used in {@link Home#onCreate(Bundle)} and {@link Menu#onCreate(Bundle)}
     * @param cas Which fragment is being initialized now
     *
     * @exception JSONException
     */
    public void getJson(String cas, Context context){
        String urlToGet = context.getString(MainActivity.API_PATH);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                urlToGet, null, response -> {
            JSONObject jsonObject;
            String dateTxt, date, name;
            try {
                int i = 0;
                while (i <= response.length()-1) {
                    jsonObject = response.getJSONObject(i);
                    if(cas.equals("menu")) {
                        date = jsonObject.getString("date");
                        dateTxt = StringEscapeUtils.unescapeJava(jsonObject.getString("dateTxt"));
                        blocksModelList.add(new HomeBlocksModel(date, dateTxt));
                        mutableLiveData.setValue(blocksModelList);
                    }else {
                        date = jsonObject.getString("date");
                        dateTxt = StringEscapeUtils.unescapeJava(jsonObject.getString("dateTxt"));
                        name = StringEscapeUtils.unescapeJava(jsonObject.getString("name"));
                        blocksModelList.add(new HomeBlocksModel(date, dateTxt, name));
                        mutableLiveData.setValue(blocksModelList);
                    }
                    i++;
                }

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
