package net.energogroup.akafist;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.energogroup.akafist.api.AzbykaAPI;
import net.energogroup.akafist.api.PrAPI;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Класс с глобальными переменными
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class AkafistApplication extends Application {

    public PrAPI prAPI;
    public AzbykaAPI azbykaAPI;

    @Override
    public void onCreate() {
        super.onCreate();

        configureRetrofit();
    }

    public void configureRetrofit() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("Content-Type", "application/json")
                            .header("User-Agent", getResources().getString(R.string.app_ver))
                            //.header("Authorization", "Bearer "+getResources().getString(R.string.api_token))//api-token
                            .method(chain.request().method(), chain.request().body())
                            .build();

                    return chain.proceed(request);
                })
                .addInterceptor(loggingInterceptor)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofitPR = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.apiPath))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        Retrofit retrofitAzbyka = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.azbykaApiPath))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        prAPI = retrofitPR.create(PrAPI.class);
        azbykaAPI = retrofitAzbyka.create(AzbykaAPI.class);
    }
}
