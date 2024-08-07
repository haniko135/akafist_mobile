package net.energogroup.akafist.api;

import net.energogroup.akafist.models.azbykaAPI.AbstractDate;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Field;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AzbykaAPI {

    @POST("api/login/")
    Single<String> login(@Body String body); // TODO: add body type

    @GET("api/day/")
    Single<AbstractDate> getDay(@Field("date[exact]") String date);

    @GET("api/cache_dates/")
    Single<String> getTextsByDate(@Field("date[exact]") String date);

    @GET("api/texts/")
    Single<String> getReadToday(@Path("id") int id);
}
