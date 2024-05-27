package net.energogroup.akafist.service;

import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private static final String PROTOCOL_CHARSET="utf-8";
    private final int method;
    private String mRequestBody;
    private String url;
    private Map<String, String> headers;
    private Map<String, Object> params;
    private final Response.Listener<T> listener;

    public GsonRequest(int method, String url,
                       @Nullable Response.ErrorListener errorListener,
                       Class<T> clazz,
                       Map<String, Object> params,
                       Response.Listener<T> listener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.listener = listener;
        if(params!=null){
            this.params = params;
            this.mRequestBody = gson.toJson(params);
        }
        this.method = method;
        this.url = url;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String getUrl() {
        if(method == Request.Method.GET) {
            StringBuilder stringBuilder = new StringBuilder(url);
            if (params != null) {
                Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
                int i = 1;
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    if(i == 1) {
                        stringBuilder.append("?").append(entry.getKey()).append("=").append(entry.getValue());
                    } else {
                        stringBuilder.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                    }
                    iterator.remove(); // avoids a ConcurrentModificationException
                    i++;
                }
                url = stringBuilder.toString();
            }
        }
        return url;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        }catch (UnsupportedEncodingException e) {
            Log.e("REQUEST ERROR", "Unsupported Encoding while trying to get the bytes of " + mRequestBody +
                    "using " + PROTOCOL_CHARSET);
            return null;
        }
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {

            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            if (clazz.getName().equals(JSONObject.class.getName())) {
                try {
                    JSONObject object = new JSONObject(json);
                    return Response.success(object, HttpHeaderParser.parseCacheHeaders(response));
                }catch (JSONException ex) {
                    return Response.error(new ParseError(ex));
                }

            } else if (clazz.getName().equals(JSONArray.class.getName())) {
                try {
                    JSONArray object = new JSONArray(json);
                    return Response.success(object, HttpHeaderParser.parseCacheHeaders(response));
                }catch (JSONException ex) {
                    return Response.error(new ParseError(ex));
                }
            } else {
                return Response.success(gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
            }
        }catch (UnsupportedEncodingException e) {
            Log.e("REQUEST ERROR", e.getMessage());
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (headers.isEmpty()) {
            return super.getHeaders();
        }else{
            return headers;
        }
    }
}
