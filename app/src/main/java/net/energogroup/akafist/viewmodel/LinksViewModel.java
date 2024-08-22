package net.energogroup.akafist.viewmodel;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.energogroup.akafist.R;
import net.energogroup.akafist.api.PrAPI;
import net.energogroup.akafist.db.StarredDTO;
import net.energogroup.akafist.fragments.LinksFragment;
import net.energogroup.akafist.models.LinksModel;
import net.energogroup.akafist.models.StarredModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A class containing data processing logic
 * {@link LinksFragment} and {@link LinksModel}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class LinksViewModel extends ViewModel {

    public static final String TAG = "LINKS_VIEW_MODEL";
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<LinksModel> linksModelList = new ArrayList<>();
    private final MutableLiveData<List<LinksModel>> mutableLinksDate = new MutableLiveData<>();
    private final List<LinksModel> downloadAudio = new ArrayList<>();
    private int image;

    /**
     * This method returns the current value of MutableLiveData<List<LinksModel>>
     * @return MutableLiveData<List<LinksModel>>
     */
    public MutableLiveData<List<LinksModel>> getMutableLinksDate() {
        return mutableLinksDate;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    /**
     * This method makes a request to the remote server
     * depending on the cas parameter and receives data
     * for output in the method {@link LinksFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * @param cas String case
     * @param inflater LayoutInflater
     */
    public void getJson(String cas, LayoutInflater inflater, PrAPI prAPI){
        if (cas.equals("links")) {
            compositeDisposable.add(
                    prAPI.getLinks()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(mutableLinksDate::setValue, error -> {
                                Log.e("Response", error.getMessage());
                            })
            );
        }
        else if(cas.equals("molitvyOfflain")){
            image = R.mipmap.ic_launcher;
            List<String> molitvyName = Arrays.asList(inflater.getContext().getResources().getStringArray(R.array.molitvy_offline_audio_name));
            List<String> molitvyLink = Arrays.asList(inflater.getContext().getResources().getStringArray(R.array.molitvy_offline_audio_link));
            for (int i=0; i<molitvyName.size(); i++){
                linksModelList.add(new LinksModel(molitvyLink.get(i), molitvyName.get(i), image));
            }
            mutableLinksDate.setValue(linksModelList);
        }
    }

    /**
     * This method is used when updating the page in
     * {@link LinksFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * @param cas String
     * @param inflater LayoutInflater
     */
    public void retryGetJson(String cas, LayoutInflater inflater, PrAPI prAPI){
        if (cas.equals("links")) {
            linksModelList = new ArrayList<>();
            getJson(cas, inflater, prAPI);
        }else if(cas.equals("molitvyOfflain")){
            linksModelList = new ArrayList<>();
            getJson(cas, inflater, prAPI);
        }
    }



    /**
     * @param audioFilesDir Final lisy destination
     * @return List<LinksModel> Downloaded audio files
     */
    @SuppressLint("SuspiciousIndentation")
    public List<LinksModel> getDownload(String audioFilesDir){
        downloadAudio.clear();
        String fullPath = audioFilesDir+"/";
        File directory = new File(fullPath);
        File[] files = directory.listFiles();
        if (files != null){
            for (File file : files) {
                String filename = file.getName().substring(0,file.getName().length()-4);
                downloadAudio.add(new LinksModel(fullPath + file.getName(), filename));
            }
        }
        return downloadAudio;
    }

    public ArrayList<StarredModel> getStarredAudio(String date, SQLiteDatabase db){
        Cursor cursorPrayers = db.rawQuery("SELECT * FROM " + StarredDTO.TABLE_NAME + " WHERE "
                + StarredDTO.COLUMN_NAME_OBJECT_TYPE + "='" + date+"'", null);
        ArrayList<StarredModel> tempStarred = new ArrayList<>();

        if(cursorPrayers.moveToFirst()){
            do{
                tempStarred.add(new StarredModel(
                        Integer.parseInt(cursorPrayers.getString(0)),
                        cursorPrayers.getString(1),
                        cursorPrayers.getString(2)
                ));
            }while (cursorPrayers.moveToNext());
        }
        cursorPrayers.close();
        return tempStarred;
    }
}
