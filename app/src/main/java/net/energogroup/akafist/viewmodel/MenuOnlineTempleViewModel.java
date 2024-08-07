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
import net.energogroup.akafist.R;
import net.energogroup.akafist.api.PrAPI;
import net.energogroup.akafist.fragments.Home;
import net.energogroup.akafist.models.HomeBlocksModel;
import net.energogroup.akafist.service.RequestServiceHandler;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MenuOnlineTempleViewModel extends ViewModel {

    private final String TAG = "MENU_ONLINE_TEMPLE_VM";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<List<HomeBlocksModel>> onlineTempleListMLD = new MutableLiveData<>();
    private List<HomeBlocksModel> onlineTempleList = new ArrayList<>();

    public MutableLiveData<List<HomeBlocksModel>> getOnlineTempleListMLD() {
        return onlineTempleListMLD;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public List<HomeBlocksModel> getOnlineTempleList() {
        return onlineTempleList;
    }

    public void initialize(PrAPI prAPI, Context context, String mode){
        if(mode.equals("menuOnlineTemple")){
            onlineTempleList.add(new HomeBlocksModel("onlineMichael",
                    context.getString(R.string.online_Michael), context.getString(R.string.online_Michael_2)));
            onlineTempleList.add(new HomeBlocksModel("onlinePokrovaPls",
                    context.getString(R.string.online_Pokrova_Pls), context.getString(R.string.online_Pokrova_Pls_2)));
            onlineTempleList.add(new HomeBlocksModel("onlineVarvara",
                    context.getString(R.string.online_Varvara), context.getString(R.string.online_Varvara_2)));
            onlineTempleListMLD.setValue(onlineTempleList);
        } else if (mode.equals("menuChurch")) {
            onlineTempleList.add(new HomeBlocksModel("molitvyOfflain",
                    context.getString(R.string.molitvy_offline), context.getString(R.string.molitvy_offline_2)));
            //getJson(context);
            getJson(prAPI);
        }
    }

    /**
     * This method sends a request to a remote server and receives a response,
     * which is later used in the methods {@link Home#onCreateView(LayoutInflater, ViewGroup, Bundle)} and
     * This method is used in {@link Home#onCreate(Bundle)}
     */
    public void getJson(PrAPI prAPI){
        compositeDisposable.add(
                prAPI.getHomeBlocks()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(success -> {
                            onlineTempleList.addAll(success);
                            onlineTempleListMLD.setValue(onlineTempleList);
                            getJsonPsaltir(prAPI);
                        }, error -> {
                            Log.e(TAG,error.getLocalizedMessage());
                        })
        );
    }

    public void getJsonPsaltir(PrAPI prAPI){
        compositeDisposable.add(
                prAPI.getPsaltir()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(success -> {
                            onlineTempleList.addAll(success);
                            onlineTempleListMLD.setValue(onlineTempleList);
                        }, error -> {
                            Log.e(TAG,error.getLocalizedMessage());
                        })
        );
    }
}
