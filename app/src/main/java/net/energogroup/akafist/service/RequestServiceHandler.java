package net.energogroup.akafist.service;

import android.util.ArrayMap;

import com.android.volley.Response;

import net.energogroup.akafist.MainActivity;

import java.util.Map;

public class RequestServiceHandler {
    private Map<String, String> mHeaders;

    public RequestServiceHandler() {
        mHeaders = new ArrayMap<>();
    }

    public void addHeader(String key, String value) {
        mHeaders.put(key,value);
    }

    public <T> void objectRequest(String url, int method, Map<String, Object> params, Class clazz,
                                  Response.Listener<T> response, Response.ErrorListener error) {

        GsonRequest objectRequest;

        if (params != null) {
            objectRequest = new GsonRequest<T>(method, url, error, clazz, params, response);
            objectRequest.setHeaders(mHeaders);
        } else {
            objectRequest = new GsonRequest<T>(method, url, error, clazz,null, response);
            objectRequest.setHeaders(mHeaders);
        }

        MainActivity.mRequestQueue.add(objectRequest);
    }
}
