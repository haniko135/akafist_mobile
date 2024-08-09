package net.energogroup.akafist.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.energogroup.akafist.api.PrAPI;
import net.energogroup.akafist.models.psaltir.PsaltirKafismaModel;
import net.energogroup.akafist.models.psaltir.PsaltirModel;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PsaltirViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<PsaltirModel> psaltirModelMLD = new MutableLiveData<>();

    public MutableLiveData<PsaltirModel> getPsaltirModelMLD() {
        return psaltirModelMLD;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public void getJson(PrAPI prAPI, int id) {
        compositeDisposable.add(
                prAPI.getPsaltirBlock(String.valueOf(id))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(psaltirModelMLD::setValue
                        , error-> {
                            Log.e("Response", error.getLocalizedMessage());
                        })
        );
    }
}
