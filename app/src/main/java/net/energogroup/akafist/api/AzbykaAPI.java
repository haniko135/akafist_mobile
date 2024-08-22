package net.energogroup.akafist.api;

import net.energogroup.akafist.models.azbykaAPI.AdditionalText;
import net.energogroup.akafist.models.azbykaAPI.AuthToken;
import net.energogroup.akafist.models.azbykaAPI.CacheDate;
import net.energogroup.akafist.models.azbykaAPI.LoginCredentials;

import java.util.ArrayList;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AzbykaAPI {

    @GET("api/day")
    Single<Response<ArrayList<CacheDate>>> getAbstractDates(@Query(value = "date[exact]") String date);

    @GET("api/cache_dates")
    Single<Response<ArrayList<CacheDate>>> getDate(@Query(value = "date[exact]") String date);

    @POST("api/login")
    Single<Response<AuthToken>> login(@Body LoginCredentials credentials);

    @GET("api/texts/{id}")
    Single<Response<AdditionalText>> getText(@Path("id") int id);
}
