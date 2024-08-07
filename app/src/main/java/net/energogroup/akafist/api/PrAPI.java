package net.energogroup.akafist.api;

import net.energogroup.akafist.models.DayModel;
import net.energogroup.akafist.models.HomeBlocksModel;
import net.energogroup.akafist.models.LinksModel;
import net.energogroup.akafist.models.PrayersModels;
import net.energogroup.akafist.models.SkypeBlockAPIModel;
import net.energogroup.akafist.models.SkypesAPIModel;
import net.energogroup.akafist.models.SkypesConfs;
import net.energogroup.akafist.models.TypesModel;
import net.energogroup.akafist.models.psaltir.PsaltirKafismaModel;
import net.energogroup.akafist.models.psaltir.PsaltirModel;
import net.energogroup.akafist.models.psaltir.PsaltirPrayerModel;

import java.util.ArrayList;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PrAPI {

    @GET("/api/church/")
    Single<ArrayList<HomeBlocksModel>> getHomeBlocks();

    @GET("/api/church/{date}")
    Single<DayModel> getDate(@Path("date") String date);

    @GET("/api/church/{date}/{text}")
    Single<PrayersModels> getText(@Path("date") String date, @Path("text") String text);

    @GET("/api/church/talks/")
    Single<ArrayList<LinksModel>> getLinks();

    @GET("/api/church/skype/")
    Single<SkypesAPIModel> getSkype();

    @GET("/api/church/skype/{block}")
    Single<SkypeBlockAPIModel> getSkypeBlock(@Path("block") String block);

    @GET("/api/psaltir/")
    Single<ArrayList<HomeBlocksModel>> getPsaltir();

    @GET("/api/psaltir/{block_id}")
    Single<PsaltirModel> getPsaltir(@Path("block_id") String block_id);

    @GET("/api/psaltir/{block_id}/{kaf_id}")
    Single<PsaltirPrayerModel> getPsaltirPrayers(@Path("block_id") String block_id, @Path("kaf_id") String kaf_id);
}
