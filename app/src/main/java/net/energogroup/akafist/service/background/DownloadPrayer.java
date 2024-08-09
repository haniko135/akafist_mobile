package net.energogroup.akafist.service.background;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.energogroup.akafist.AkafistApplication;
import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.api.PrAPI;
import net.energogroup.akafist.db.PrayersDTO;
import net.energogroup.akafist.db.StarredDTO;
import net.energogroup.akafist.models.PrayersModels;
import net.energogroup.akafist.models.psaltir.PsalmModel;
import net.energogroup.akafist.models.psaltir.SlavaModel;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DownloadPrayer {

    public static final String TAG = "DOWNLOAD_PRAYER";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String prevMenu;
    private Integer prayerId;
    private MainActivity mainActivity;
    private SQLiteDatabase db;


    public void setData(String prevMenu, Integer prayerId){
        this.prevMenu = prevMenu;
        this.prayerId = prayerId;
    }

    public void init(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        db = mainActivity.getDbHelper().getWritableDatabase();
    }

    public void downloadPrayer() {
        PrAPI prAPI = ((AkafistApplication)mainActivity.getApplication()).prAPI;
        compositeDisposable.add(
            prAPI.getText(prevMenu, String.valueOf(prayerId))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::toDB, error->{
                        Log.e(TAG, error.getLocalizedMessage());
                    })
        );
    }

    public void downloadPsaltir(){
        PrAPI prAPI = ((AkafistApplication)mainActivity.getApplication()).prAPI;
        compositeDisposable.add(
                prAPI.getPsaltirPrayers(prevMenu, String.valueOf(prayerId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(success->{
                            PrayersModels model = new PrayersModels();

                            if(!success.getPsalms().isEmpty()){
                                success.getPsalms().forEach(psalmModel -> {
                                    if(psalmModel.getSlava() != null){
                                        SlavaModel slavaModel = new SlavaModel();
                                        slavaModel.setText(psalmModel.getSlava().getText());
                                        if(psalmModel.getSlava().getPrayer() != null){
                                            slavaModel.setPrayer(psalmModel.getSlava().getPrayer());
                                        }
                                        psalmModel.setSlava(slavaModel);
                                    }
                                });

                                model = makingModel(success.getName(),
                                        StringEscapeUtils.unescapeJava(success.getKafismaStart()),
                                        StringEscapeUtils.unescapeJava(success.getKafismaEnd()),
                                        success.getPrev(),
                                        success.getNext(),
                                        success.getPsalms());
                                Log.d(TAG, model.toString());
                            }else {
                                model = new PrayersModels(
                                        success.getName(),
                                        StringEscapeUtils.unescapeJava(success.getText()),
                                        success.getPrev(),
                                        success.getNext());
                                Log.d(TAG, model.toString());
                            }
                            toDB(model);
                        }, error->{
                            Log.e(TAG, error.getLocalizedMessage());
                        })
        );
    }

    private void toDB(PrayersModels success){
        ContentValues contentValues = new ContentValues();
        contentValues.put(PrayersDTO.COLUMN_NAME_ID, prayerId);
        contentValues.put(PrayersDTO.COLUMN_NAME_NAME, prevMenu+"/"+success.getName());
        contentValues.put(PrayersDTO.COLUMN_NAME_TEXT, success.getHtml());
        contentValues.put(PrayersDTO.COLUMN_NAME_PREV, success.getPrev());
        contentValues.put(PrayersDTO.COLUMN_NAME_NEXT, success.getNext());

        db.insert(PrayersDTO.TABLE_NAME,null, contentValues);
        Log.e(TAG, "toDB: done");
    }

    public void deletePrayer(String prevMenuData, String name){
        String[] selectionArgs = { prevMenuData+"/"+name };

        db.delete(PrayersDTO.TABLE_NAME,PrayersDTO.COLUMN_NAME_NAME + " LIKE ?",
                selectionArgs);
    }

    public PrayersModels makingModel(String name, String kafStart, String kafEnd, Integer prev,
                            Integer next, List<PsalmModel> psalms) {
        StringBuilder tempHtml = new StringBuilder();
        tempHtml.append(kafStart);
        for (PsalmModel item : psalms) {
            tempHtml.append(item.getText());
            if (item.getSlava() != null){
                tempHtml.append(item.getSlava().getText());
                if(!item.getSlava().getPrayer().isEmpty()){
                    tempHtml.append(item.getSlava().getPrayer());
                }
            }
        }
        tempHtml.append(kafEnd);

        return new PrayersModels(name, tempHtml.toString(), prev, next);
    }
}
