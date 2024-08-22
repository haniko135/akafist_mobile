package net.energogroup.akafist.viewmodel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.energogroup.akafist.api.PrAPI;
import net.energogroup.akafist.fragments.PrayerFragment;
import net.energogroup.akafist.models.PrayersModels;
import net.energogroup.akafist.models.psaltir.PsalmModel;
import net.energogroup.akafist.models.psaltir.PsaltirPrayerModel;
import net.energogroup.akafist.models.psaltir.SlavaModel;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A class containing data processing logic
 * {@link PrayerFragment} and {@link PrayersModels}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class PrayerViewModel extends ViewModel {

    private final String TAG = "PRAYER_VIEW_MODEL";
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private PrayersModels prayersModel;
    private MutableLiveData<PrayersModels> prayersModelsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRendered = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isRenderedTextArr = new MutableLiveData<>(false);

    private final MutableLiveData<ArrayList<String>> prayerTextArrMLD = new MutableLiveData<>();

    /**
     * @return Current prayer
     */
    public PrayersModels getPrayersModel() {
        return prayersModel;
    }

    /**
     * @return Current array of prayers
     */
    public MutableLiveData<PrayersModels> getPrayersModelsMutableLiveData() {
        return prayersModelsMutableLiveData;
    }

    public void setPrayersModelsMutableLiveData(MutableLiveData<PrayersModels> prayersModels) {
        this.prayersModelsMutableLiveData = prayersModels;
        formattingText(Objects.requireNonNull(this.prayersModelsMutableLiveData.getValue()));
    }

    public MutableLiveData<Boolean> getIsRendered() { return isRendered; }

    public MutableLiveData<ArrayList<String>> getPrayerTextArrMLD() {
        return prayerTextArrMLD;
    }

    public void setIsRenderedTextArr(Boolean isRenderedTextArrTemp){
        this.isRenderedTextArr.setValue(isRenderedTextArrTemp);
    }

    public MutableLiveData<Boolean> getIsRenderedTextArr() {
        return isRenderedTextArr;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    /**
     * This method sends a request to a remote server and receives a response, which later
     * used in methods {@link PrayerFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * This method is used in {@link PrayerFragment#onCreate(Bundle)}
     * @param date - Previous page type
     * @param id - Prayer id
     */
    public void getJson(PrAPI prAPI, String date, String id){
        isRendered.setValue(false);
        compositeDisposable.add(
                prAPI.getText(date, id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(success -> {
                            prayersModel = new PrayersModels();
                            prayersModel.setName(StringEscapeUtils.unescapeJava(success.getName()));
                            prayersModel.setHtml(StringEscapeUtils.unescapeJava(success.getHtml()));
                            prayersModel.setNext(success.getNext());
                            prayersModel.setPrev(success.getPrev());
                            prayersModelsMutableLiveData.setValue(prayersModel);
                            formattingText(prayersModel);
                        }, error->{
                            Log.e(TAG, error.getLocalizedMessage());
                        })
        );
    }

    public void getJsonPsaltir(PrAPI prAPI, int blockId, int kafId){
        isRendered.setValue(false);
        compositeDisposable.add(
                prAPI.getPsaltirPrayers(String.valueOf(blockId), String.valueOf(kafId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(success -> {
                            PsaltirPrayerModel psaltirPrayerModel = new PsaltirPrayerModel();
                            psaltirPrayerModel.setName(StringEscapeUtils.unescapeJava(success.getName()));

                            // ДОДЕЛАТЬ В АПИ НОРМ ВЫВОД!!!
                            if(!success.getKafismaStart().isEmpty()){
                                psaltirPrayerModel.setKafismaStart(StringEscapeUtils.unescapeJava(success.getKafismaStart()));
                            }
                            if(!success.getKafismaEnd().isEmpty()){
                                psaltirPrayerModel.setKafismaEnd(StringEscapeUtils.unescapeJava(success.getKafismaEnd()));
                            }

                            psaltirPrayerModel.setText(StringEscapeUtils.unescapeJava(success.getText()));
                            psaltirPrayerModel.setPrev(success.getPrev());
                            psaltirPrayerModel.setNext(success.getNext());

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
                                psaltirPrayerModel.setPsalms(success.getPsalms());

                                makingModel(psaltirPrayerModel.getName(),
                                        psaltirPrayerModel.getKafismaStart(),
                                        psaltirPrayerModel.getKafismaEnd(),
                                        psaltirPrayerModel.getPrev(),
                                        psaltirPrayerModel.getNext(),
                                        psaltirPrayerModel.getPsalms());
                            }else {
                                prayersModel = new PrayersModels(
                                        psaltirPrayerModel.getName(),
                                        psaltirPrayerModel.getText(),
                                        psaltirPrayerModel.getPrev(),
                                        psaltirPrayerModel.getNext());
                                prayersModelsMutableLiveData.setValue(prayersModel);
                                formattingText(prayersModel);
                            }
                        }, error->{
                            Log.e(TAG, error.getLocalizedMessage());
                        })
        );
    }


    public void makingModel(String name, String kafStart, String kafEnd, Integer prev,
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

        prayersModel = new PrayersModels(name, tempHtml.toString(), prev, next);
        prayersModelsMutableLiveData.setValue(prayersModel);
        formattingText(prayersModel);
    }


    public void formattingText(PrayersModels prayersModel){
        String[] prayerTexts = prayersModel.getHtml().split("(?=<details>)");
        ArrayList<String> prayerTextArr = new ArrayList<>();
        for (String prayerText : prayerTexts) {
            String[] temp = prayerText.split("(?<=</details>)");
            if (temp.length != 0) {
                prayerTextArr.addAll(Arrays.asList(temp));
            }
        }
        prayerTextArrMLD.setValue(prayerTextArr);
        isRendered.setValue(true);
    }
}
