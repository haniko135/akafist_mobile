package net.energogroup.akafist.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Class, containing authorization logic
 * @author Nastya Izotina
 * @version 1.2.0
 */
public class LoginViewModel extends ViewModel {
    private static final String DEV_TAG = "LoginViewModel";
    private MutableLiveData<String> authorizationURL = new MutableLiveData<>();
    private MutableLiveData<String> accessToken = new MutableLiveData<>();
    private MutableLiveData<String> refreshToken = new MutableLiveData<>();
    private MutableLiveData<String> nameMLD = new MutableLiveData<>();
    private MutableLiveData<String> emailMLD = new MutableLiveData<>();
    private MutableLiveData<Boolean> isHostUnavailable = new MutableLiveData<>(false);
    private String codeVerifier;


    public MutableLiveData<String> getAuthorizationURL() {
        return authorizationURL;
    }

    public MutableLiveData<Boolean> getIsHostUnavailable() {
        return isHostUnavailable;
    }

    public MutableLiveData<String> getNameMLD() { return nameMLD; }

    public MutableLiveData<String> getEmailMLD() { return emailMLD; }

    /**
     * First method to authentication
     * @param context Current fragment context
     */
    public void getFirst(Context context){
        String url = context.getString(R.string.first_url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            String codeVerif, authURL;
            try {
                codeVerif = response.getString("codeVerifier");
                authURL = response.getString("authorizationUrl");
                Log.e("AUTH", codeVerif+"\n"+authURL);
                codeVerifier = codeVerif;
                authorizationURL.setValue(authURL);
                isHostUnavailable.setValue(false);
                Log.e(DEV_TAG, "isHostUnavailable = false");
            } catch (JSONException e) {
                Log.e(DEV_TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }

        }, error -> {
            if (error.networkResponse == null){
                isHostUnavailable.setValue(true);
                Log.e(DEV_TAG, "isHostUnavailable = true");
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", context.getString(R.string.app_ver));
                headers.put("Connection", "keep-alive");
                return headers;
            }
        };

        MainActivity.mRequestQueue.add(request);
    }

    /**
     * Second method for authentication
     * @param authCode Authcode from previous step
     * @param context Current fragment context
     */
    public void getSecond(String authCode, Context context){
        try {
            String url = context.getString(R.string.first_url);
            JSONObject object = new JSONObject();
            object.put("codeVerifier", codeVerifier);
            object.put("authCode", authCode);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object , response -> {
                String accToken, refrToken;
                try {
                    accToken = response.getString("accessToken");
                    refrToken = response.getString("refreshToken");
                    accessToken.setValue(accToken);
                    refreshToken.setValue(refrToken);

                    Log.e("URL_RF", accessToken.getValue());
                    Log.e("URL_RF", "DONE!");

                    getThird(context);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, Throwable::printStackTrace){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("User-Agent", context.getString(R.string.app_ver));
                    headers.put("Connection", "keep-alive");
                    return headers;
                }
            };

            MainActivity.mRequestQueue.add(request);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Third method for authentication
     * @param context Current fragment context
     */
    public void getThird(Context context){
        String url = context.getString(R.string.user_info);
        Log.e("URL_RF", accessToken.getValue());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            String name, email;
            try {
                Log.e("URL_RF", response.toString());

                JSONObject object = response.getJSONObject("element");
                name = object.getString("userName");
                email = object.getString("email");
                nameMLD.setValue(name);
                emailMLD.setValue(email);

                Log.e("URL_RF", "Done!");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            if (error.networkResponse.statusCode == 401){
                Log.e("URL_RF", "401 error");
                getFourth(context);
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", context.getString(R.string.app_ver));
                headers.put("Connection", "keep-alive");
                headers.put("Authorization", "Bearer " + accessToken.getValue());
                return headers;
            }
        };

        MainActivity.mRequestQueue.add(request);
    }

    /**
     * Fourth method for authentication
     * @param context Current fragment context
     */
    public void getFourth(Context context){
        try {
            String url = context.getString(R.string.first_url)+"&"+context.getString(R.string.refresh_url);
            JSONObject object = new JSONObject();
            object.put("refreshToken", refreshToken.getValue());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object , response -> {
                String accToken, refrToken;
                try {
                    accToken = response.getString("accessToken");
                    refrToken = response.getString("refreshToken");
                    accessToken.setValue(accToken);
                    refreshToken.setValue(refrToken);

                    Log.e("URL_RF", accessToken.getValue());
                    Log.e("URL_RF", "Ah shit here we go again");

                    getThird(context);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, Throwable::printStackTrace){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("User-Agent", context.getString(R.string.app_ver));
                    headers.put("Connection", "keep-alive");
                    return headers;
                }
            };

            MainActivity.mRequestQueue.add(request);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
