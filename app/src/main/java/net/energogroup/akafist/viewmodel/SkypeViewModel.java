package net.energogroup.akafist.viewmodel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.energogroup.akafist.api.PrAPI;
import net.energogroup.akafist.fragments.SkypesFragment;
import net.energogroup.akafist.fragments.SkypesBlocksFragment;
import net.energogroup.akafist.models.SkypesConfs;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A class containing data processing logic
 * {@link SkypesBlocksFragment} and {@link SkypesFragment}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class SkypeViewModel extends ViewModel {

    private final String TAG = "SKYPE_VM";
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final List<SkypesConfs> confsModels = new ArrayList<>();
    private final MutableLiveData<List<SkypesConfs>> confsMutableLiveData = new MutableLiveData<>();

    /**
     * @return Current conferences
     */
    public MutableLiveData<List<SkypesConfs>> getConfsMutableLiveData() {
        return confsMutableLiveData;
    }

    /**
     * This method sends a request to a remote server and receives a response, which later
     * used in the method {@link SkypesFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * This method is used in {@link SkypesFragment#onCreate(Bundle)}
     */
    public void getJsonSkype(PrAPI prAPI){
        compositeDisposable.add(prAPI.getSkype()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    confsModels.addAll(success.getConfs());
                    confsModels.addAll(success.getBlocks());
                    confsMutableLiveData.setValue(confsModels);
                }, error->{
                   Log.e(TAG, error.getLocalizedMessage());
                })
        );
    }

    /**
     * This method sends a request to a remote server and receives a response, which later
     * used in the method {@link SkypesBlocksFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * This method is used in {@link SkypesBlocksFragment#onCreate(Bundle)}
     * @param urlId Conference ID
     */
    public void getJsonSkypeBlock(PrAPI prAPI, int urlId){
        compositeDisposable.add(prAPI.getSkypeBlock(String.valueOf(urlId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    confsModels.addAll(success.getConfs());
                    confsMutableLiveData.setValue(confsModels);
                }, error->{
                    Log.e(TAG, error.getLocalizedMessage());
                })
        );
    }
}
