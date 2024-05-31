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
import net.energogroup.akafist.fragments.SkypesFragment;
import net.energogroup.akafist.fragments.SkypesBlocksFragment;
import net.energogroup.akafist.models.SkypesConfs;
import net.energogroup.akafist.service.RequestServiceHandler;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A class containing data processing logic
 * {@link SkypesBlocksFragment} and {@link SkypesFragment}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class SkypeViewModel extends ViewModel {
    private final List<SkypesConfs> skypeModels = new ArrayList<>();
    private final List<SkypesConfs> confsModels = new ArrayList<>();
    private final MutableLiveData<List<SkypesConfs>> skypesMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<SkypesConfs>> confsMutableLiveData = new MutableLiveData<>();

    /**
     * @return Current conference groups
     */
    public MutableLiveData<List<SkypesConfs>> getSkypesMutableLiveData() {
        return skypesMutableLiveData;
    }

    /**
     * @return Current conferences
     */
    public MutableLiveData<List<SkypesConfs>> getConfsMutableLiveData() {
        return confsMutableLiveData;
    }

    /**
     * This method sends a request to a remote server and receives a response, which later
     * used in the method {@link SkypesFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * This method is used in {@link SkypesFragment#onCreate(Bundle)}
     */
    public void getJsonSkype(Context context){
        String urlToGet2 = context.getString(MainActivity.API_PATH)+"skype";

        RequestServiceHandler serviceHandler = new RequestServiceHandler();
        serviceHandler.addHeader("User-Agent", context.getString(MainActivity.APP_VER));
        serviceHandler.addHeader("Connection", "keep-alive");

        serviceHandler.objectRequest(urlToGet2, Request.Method.GET,
                null, JSONObject.class,
                (Response.Listener<JSONObject>) response -> {
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
                            i++;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("Response", error.getMessage()));
    }

    /**
     * This method sends a request to a remote server and receives a response, which later
     * used in the method {@link SkypesBlocksFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * This method is used in {@link SkypesBlocksFragment#onCreate(Bundle)}
     * @param urlId Conference ID
     * @exception JSONException
     */
    public void getJsonSkypeBlock(int urlId, Context context){
        String urlToGet = context.getString(MainActivity.API_PATH)+"skype/"+urlId;

        RequestServiceHandler serviceHandler = new RequestServiceHandler();
        serviceHandler.addHeader("User-Agent", context.getString(MainActivity.APP_VER));
        serviceHandler.addHeader("Connection", "keep-alive");

        serviceHandler.objectRequest(urlToGet, Request.Method.GET,
                null, JSONObject.class,
                (Response.Listener<JSONObject>) response -> {
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
                }, error -> Log.e("Response", error.getMessage()));
    }
}
