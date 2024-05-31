package net.energogroup.akafist.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.Response;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.models.psaltir.PsaltirKafismaModel;
import net.energogroup.akafist.models.psaltir.PsaltirModel;
import net.energogroup.akafist.service.RequestServiceHandler;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PsaltirViewModel extends ViewModel {

    private PsaltirModel psaltirModel;
    private final MutableLiveData<PsaltirModel> psaltirModelMLD = new MutableLiveData<>();

    public MutableLiveData<PsaltirModel> getPsaltirModelMLD() {
        return psaltirModelMLD;
    }

    public void getJson(Context context, int id) {
        String url = context.getString(MainActivity.API_PATH_PSALTIR)+ id;

        RequestServiceHandler serviceHandler = new RequestServiceHandler();
        serviceHandler.addHeader("User-Agent", context.getString(MainActivity.APP_VER));
        serviceHandler.addHeader("Connection", "keep-alive");

        serviceHandler.objectRequest(url, Request.Method.GET,
                null, JSONObject.class,
                (Response.Listener<JSONObject>) response -> {
                    int id1;
                    String desc, name;
                    JSONArray kafismas;
                    try {
                        id1 = response.getInt("id");
                        name = StringEscapeUtils.unescapeJava(response.getString("name"));
                        desc = StringEscapeUtils.unescapeJava(response.getString("desc"));
                        kafismas = response.getJSONArray("kafismas");
                        if(kafismas.length() > 0) {
                            int j = 0;
                            int id2;
                            String name2, desc2;
                            JSONObject object;
                            List<PsaltirKafismaModel> psaltirKafismaModels = new ArrayList<>();
                            while (j <= kafismas.length()-1){
                                object = kafismas.getJSONObject(j);
                                id2 = object.getInt("id");
                                name2 = object.getString("name");
                                desc2 = object.getString("desc");
                                psaltirKafismaModels.add(new PsaltirKafismaModel(id2, name2, desc2));
                                j++;
                            }
                            psaltirModel = new PsaltirModel(id1, name, desc, psaltirKafismaModels);
                        }else {
                            psaltirModel = new PsaltirModel(id1, name,desc);
                        }
                        psaltirModelMLD.setValue(psaltirModel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("Response", error.getMessage()));
    }

}
