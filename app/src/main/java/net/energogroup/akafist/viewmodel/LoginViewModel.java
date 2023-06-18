package net.energogroup.akafist.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import net.energogroup.akafist.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<String> authorizationURL = new MutableLiveData<>();
    private MutableLiveData<String> accessToken = new MutableLiveData<>();
    private MutableLiveData<String> refreshToken = new MutableLiveData<>();
    private MutableLiveData<String> nameMLD = new MutableLiveData<>();
    private MutableLiveData<String> emailMLD = new MutableLiveData<>();
    private MutableLiveData<Boolean> isHostUnavailable = new MutableLiveData<>();
    private String codeVerifier;


    public MutableLiveData<String> getAuthorizationURL() {
        return authorizationURL;
    }

    public MutableLiveData<Boolean> getIsHostUnavailable() { return isHostUnavailable; }

    public MutableLiveData<String> getNameMLD() { return nameMLD; }

    public MutableLiveData<String> getEmailMLD() { return emailMLD; }

    public void getFirst(){
        String url = "https://dev-knowledge-api.energogroup.org/auth/login?processtype=StandaloneApplication";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            String codeVerif, authURL;
            try {
                codeVerif = response.getString("codeVerifier");
                authURL = response.getString("authorizationUrl");
                codeVerifier = codeVerif;
                authorizationURL.setValue(authURL);
                isHostUnavailable.setValue(false);
                Log.e("LVM", "in request");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            if (error.networkResponse == null){
                isHostUnavailable.setValue(true);
                Log.e("LVM", "inerror");
            }
        }){
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

    public void getSecond(String authCode){
        try {
            String url = "https://dev-knowledge-api.energogroup.org/auth/login?processtype=StandaloneApplication";
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

                    getThird();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, Throwable::printStackTrace){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("User-Agent", "akafist_app_1.0.0");
                    headers.put("Connection", "keep-alive");
                    return headers;
                }
            };

            MainActivity.mRequestQueue.add(request);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getThird(){
        String url = "https://dev-knowledge-api.energogroup.org/api/v1/userinfo";
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
                getFourth();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "akafist_app_1.0.0");
                headers.put("Connection", "keep-alive");
                headers.put("Authorization", "Bearer " + accessToken.getValue());
                return headers;
            }
        };

        MainActivity.mRequestQueue.add(request);
    }

    public void getFourth(){
        try {
            String url = "https://dev-knowledge-api.energogroup.org/auth/login?processtype=StandaloneApplication&viaRefreshToken=true";
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

                    getThird();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, Throwable::printStackTrace){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("User-Agent", "akafist_app_1.0.0");
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
