package net.energogroup.akafist.viewmodel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.energogroup.akafist.api.PrAPI;
import net.energogroup.akafist.fragments.ChurchFragment;
import net.energogroup.akafist.models.ServicesModel;
import net.energogroup.akafist.models.TypesModel;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A class containing data processing logic of
 * {@link ChurchFragment}, {@link TypesModel} and {@link ServicesModel}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class ChurchViewModel extends ViewModel{

    private static final String TAG = "CHURCH_VM";
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final List<TypesModel> typesModelList = new ArrayList<>();
    private final MutableLiveData<List<TypesModel>> mutableTypesList = new MutableLiveData<>();
    private final List<ServicesModel> servicesModelList = new ArrayList<>();
    private final MutableLiveData<List<ServicesModel>> mutableServicesList = new MutableLiveData<>();
    private final MutableLiveData<Integer> curId = new MutableLiveData<>();
    private final MutableLiveData <String> liveDataTxt = new MutableLiveData<>();
    private final MutableLiveData <String> liveNameTxt = new MutableLiveData<>();

    /**
     * @param id Current type id
     */
    public void setCurId(int id){
        curId.setValue(id);
    }

    /**
     * @return Current list of types
     */
    public MutableLiveData<List<TypesModel>> getMutableTypesList() {
        return mutableTypesList;
    }

    /**
     * @return Current type id
     */
    public MutableLiveData<Integer> getCurId() {
        return curId;
    }

    /**
     * @return Current Prayer List
     */
    public MutableLiveData<List<ServicesModel>> getMutableServicesList() {
        return mutableServicesList;
    }

    /**
     * @return Current upper block name
     */
    public MutableLiveData<String> getLiveDataTxt() {
        return liveDataTxt;
    }

    /**
     * @return Current lower block name
     */
    public MutableLiveData<String> getLiveNameTxt() {
        return liveNameTxt;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    /**
     * This method sends a request to a remote server and receives a response,
     * which is subsequently used in the method {@link ChurchFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * This method is used in {@link ChurchFragment#onCreate(Bundle)}
     * @param date Page type
     */
    public void getJson(PrAPI prAPI, String date){
        compositeDisposable.add(
                prAPI.getDate(date)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(success -> {
                            liveDataTxt.setValue(success.getDateTxt());
                            liveNameTxt.setValue(success.getName());

                            typesModelList.addAll(success.getTypes());
                            typesModelList.forEach(typesModel -> {
                                typesModel.setName(StringEscapeUtils.unescapeJava(typesModel.getName()));
                            });
                            mutableTypesList.setValue(typesModelList);

                            servicesModelList.addAll(success.getServices());
                            servicesModelList.forEach(servicesModel -> {
                                servicesModel.setName(StringEscapeUtils.unescapeJava(servicesModel.getName()));
                                servicesModel.setDate(success.getDate());
                            });
                            mutableServicesList.setValue(servicesModelList);
                        }, error-> {
                            Log.e(TAG, error.getMessage());
                        })
        );
    }
}
