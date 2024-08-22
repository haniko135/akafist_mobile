package net.energogroup.akafist.viewmodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import net.energogroup.akafist.api.PrAPI;
import net.energogroup.akafist.db.StarredDTO;
import net.energogroup.akafist.models.PrayersModels;
import net.energogroup.akafist.models.ServicesModel;
import net.energogroup.akafist.models.StarredModel;
import net.energogroup.akafist.models.psaltir.PsalmModel;
import net.energogroup.akafist.models.psaltir.PsaltirPrayerModel;
import net.energogroup.akafist.models.psaltir.SlavaModel;

import org.apache.commons.lang.StringEscapeUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class StarredViewModel extends ViewModel {

    private static final String TAG = "STARRED_VIEW_MODEL";
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<List<ServicesModel>> textPrayers = new MutableLiveData<>();
    private final MutableLiveData<List<ServicesModel>> prayerRules = new MutableLiveData<>();
    private final List<PrayersModels> prayersCollection = new ArrayList<>();
    private final MutableLiveData<List<PrayersModels>> mutablePrayersCollection = new MutableLiveData<>();
    private MutableLiveData<PrayersModels> prayersModels = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isConverted = new MutableLiveData<>(false);
    private final List<PsalmModel> psalmModels = new ArrayList<>();
    private String nameKaf, fullHtml, kafStart, kafEnd, text;
    private Integer prev, next;




    public MutableLiveData<List<ServicesModel>> getTextPrayers() { return textPrayers; }

    public MutableLiveData<List<ServicesModel>> getPrayerRules() {
        return prayerRules;
    }

    public MutableLiveData<PrayersModels> getPrayersModels() {
        return prayersModels;
    }

    public MutableLiveData<Boolean> getIsConverted() {
        return isConverted;
    }

    public void getStarred(SQLiteDatabase db){
        Cursor cursorPrayers = db.rawQuery("SELECT * FROM " + StarredDTO.TABLE_NAME + " WHERE "
                + StarredDTO.COLUMN_NAME_OBJECT_TYPE + "='text-prayers'", null);
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
        List<ServicesModel> tempModels = new ArrayList<>();
        for (StarredModel item:tempStarred) {
            String[] objectUrl = item.getObjectUrl().split("/");
            tempModels.add(new ServicesModel(
                    objectUrl[0],
                    objectUrl[1],
                    Integer.parseInt(objectUrl[2])
            ));
        }
        textPrayers.setValue(tempModels);
    }

    public void savePrayerRuleArray(List<ServicesModel> prayerRuleText, SQLiteDatabase db){
        String[] selectionArgs = { "prayer_rule" };
        Cursor prayerRule = db.rawQuery("SELECT * FROM "+StarredDTO.TABLE_NAME+" WHERE "+StarredDTO.COLUMN_NAME_OBJECT_TYPE+"='prayer_rule'",null);
        if(prayerRule.moveToFirst()){
            db.delete(StarredDTO.TABLE_NAME,StarredDTO.COLUMN_NAME_OBJECT_TYPE + " LIKE ?", selectionArgs);
        }
        prayerRule.close();

        List<String> tempStrings = new ArrayList<>();
        Gson gson1 = new Gson();
        prayerRuleText.forEach((item)-> tempStrings.add(gson1.toJson(item)));
        Gson gson = new Gson();
        String string = gson.toJson(tempStrings);

        ContentValues contentValues = new ContentValues();
        contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_URL, string);
        contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_TYPE, "prayer_rule");
        contentValues.put(StarredDTO.COLUMN_NAME_ID, Math.round(Math.random()*1000));
        db.insert(StarredDTO.TABLE_NAME, null, contentValues);
    }

    public boolean getPrayerRuleArray(SQLiteDatabase db){
        Cursor prayerRule = db.rawQuery("SELECT * FROM "+StarredDTO.TABLE_NAME+" WHERE "+StarredDTO.COLUMN_NAME_OBJECT_TYPE+"='prayer_rule'",null);
        String stringArr;
        boolean isPrayerRule;

        if(prayerRule.moveToFirst()){
            do{
                stringArr = prayerRule.getString(2);
            }while (prayerRule.moveToNext());
            Gson gson = new Gson();
            Type servicesModelsListType = new TypeToken<List<String>>(){}.getType();
            List<String> servicesModelsString = gson.fromJson(stringArr,servicesModelsListType);
            Type servicesModelsType = new TypeToken<ServicesModel>(){}.getType();
            List<ServicesModel> models= new ArrayList<>();
            servicesModelsString.forEach((string)-> models.add(gson.fromJson(string, servicesModelsType)));

            prayerRules.setValue(models);
            isPrayerRule = true;
        }else{
            isPrayerRule = false;
        }

        prayerRule.close();
        return isPrayerRule;
    }

    public MutableLiveData<List<PrayersModels>> convertToPrayersModels(Context context, SQLiteDatabase db, PrAPI prApi){
        isConverted.setValue(false);

        if(mutablePrayersCollection.getValue() == null) {
            String[] selectionArgs = { "prayer_rule_col" };

            db.delete(StarredDTO.TABLE_NAME,StarredDTO.COLUMN_NAME_OBJECT_TYPE + " LIKE ?",
                    selectionArgs);

            List<ServicesModel> tempModels = prayerRules.getValue();

            for (int i = 0; i < tempModels.size(); i++) {

                if (i == 0) {
                    getJsonSaved(prApi, tempModels.get(i).getDate(),
                            tempModels.get(i).getId(),
                            tempModels.get(i + 1).getId(), 0);
                } else if (i == tempModels.size() - 1) {
                    getJsonSaved(prApi, tempModels.get(i).getDate(),
                            tempModels.get(i).getId(),
                            0, tempModels.get(i - 1).getId());
                } else {
                    getJsonSaved(prApi, tempModels.get(i).getDate(),
                            tempModels.get(i).getId(),
                            tempModels.get(i + 1).getId(),
                            tempModels.get(i - 1).getId());
                }
            }
        }
        //return isConverted;
        return mutablePrayersCollection;
    }

    /*public void getJsonSaved(String date, int id, Context context, int nextId, int prevId){
        String urlToGet = context.getString(MainActivity.API_PATH)+date+"/"+id;
        String urlToGetPsaltir = context.getString(MainActivity.API_PATH_PSALTIR)+1+"/"+id; //костыль что block_id всегда равен 1

        RequestServiceHandler serviceHandler = new RequestServiceHandler();
        serviceHandler.addHeader("User-Agent", context.getString(MainActivity.APP_VER));
        serviceHandler.addHeader("Connection", "keep-alive");

        if(!Objects.equals(date, "psaltir")) {
            serviceHandler.objectRequest(urlToGet, Request.Method.GET,
                    null, JSONObject.class,
                    (Response.Listener<JSONObject>) response -> {
                        int idNew, prev, next;
                        String name, html;
                        try {
                            idNew = id;
                            name = StringEscapeUtils.unescapeJava(response.getString("name"));
                            html = StringEscapeUtils.unescapeJava(response.getString("html"));
                            prev = prevId;
                            next = nextId;

                            prayersCollection.add(new PrayersModels(idNew, name, html, prev, next));
                            mutablePrayersCollection.setValue(prayersCollection);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Log.e("Response", error.getMessage()));
        }else {
            serviceHandler.objectRequest(urlToGetPsaltir, Request.Method.GET,
                    null, JSONObject.class,
                    (Response.Listener<JSONObject>) response ->{
                        JSONArray psalms;
                        try {
                            nameKaf = StringEscapeUtils.unescapeJava(response.getString("name"));
                            kafStart = StringEscapeUtils.unescapeJava(response.getString("kafismaStart"));
                            kafEnd = StringEscapeUtils.unescapeJava(response.getString("kafismaEnd"));
                            text = StringEscapeUtils.unescapeJava(response.getString("text"));
                            prev = prevId;
                            next = nextId;

                            psalms = response.getJSONArray("psalms");
                            if(psalms.length() > 0) {
                                int i = 0;
                                JSONObject obj;
                                int id2;
                                String name1, text1;
                                while (i < psalms.length() - 1) {
                                    obj = psalms.getJSONObject(i);
                                    id2 = obj.getInt("id");
                                    name1 = obj.getString("name");
                                    text1 = obj.getString("text");

                                    try {
                                        JSONObject slava = obj.getJSONObject("slava");
                                        SlavaModel slavaModel = new SlavaModel();
                                        slavaModel.setText(slava.getString("text"));
                                        if (!slava.isNull("prayer")) {
                                            slavaModel.setPrayer(slava.getString("prayer"));
                                        }
                                        psalmModels.add(new PsalmModel(id2, name1, text1, slavaModel));
                                    }catch (JSONException e){
                                        psalmModels.add(new PsalmModel(id2, name1, text1));
                                    }
                                    i++;
                                }

                                makingModel(id, nameKaf, kafStart, kafEnd,prev, next, psalmModels);
                            }else {
                                PrayersModels prayersModel = new PrayersModels(id, nameKaf, text, prev, next);
                                prayersCollection.add(prayersModel);
                                mutablePrayersCollection.setValue(prayersCollection);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }, error -> Log.e("Response",error.getMessage()));
        }
    }*/

    public void getJsonSaved(PrAPI prAPI, String date, int id, int nextId, int prevId){
        if(!Objects.equals(date, "psaltir")) {
            compositeDisposable.add(
                    prAPI.getPsaltirPrayers(String.valueOf(1), String.valueOf(id))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(success->{
                                prayersCollection.add(new PrayersModels(id, success.getName(), success.getText(), prev, next));
                                mutablePrayersCollection.setValue(prayersCollection);
                            }, error->{
                                Log.e(TAG, error.getLocalizedMessage());
                            })
            );
        }else {
            compositeDisposable.add(
                    prAPI.getPsaltirPrayers(String.valueOf(1), String.valueOf(id))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(success->{
                                PsaltirPrayerModel psaltirPrayerModel = new PsaltirPrayerModel();
                                psaltirPrayerModel.setName(StringEscapeUtils.unescapeJava(success.getName()));

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

                                    makingModel(id,
                                            psaltirPrayerModel.getName(),
                                            psaltirPrayerModel.getKafismaStart(),
                                            psaltirPrayerModel.getKafismaEnd(),
                                            psaltirPrayerModel.getPrev(),
                                            psaltirPrayerModel.getNext(),
                                            psaltirPrayerModel.getPsalms());
                                }else {
                                    PrayersModels prayersModel = new PrayersModels(id,
                                            psaltirPrayerModel.getName(),
                                            psaltirPrayerModel.getText(),
                                            psaltirPrayerModel.getPrev(),
                                            psaltirPrayerModel.getNext());
                                    prayersCollection.add(prayersModel);
                                    mutablePrayersCollection.setValue(prayersCollection);
                                }
                            }, error->{
                                Log.e(TAG, error.getLocalizedMessage());
                            })
            );
        }
    }

    public MutableLiveData<PrayersModels> getPrayerModelsCollectionItem(int id, SQLiteDatabase db){
        Cursor getPrayerCollection = db.rawQuery("SELECT * FROM "+StarredDTO.TABLE_NAME+" WHERE "+StarredDTO.COLUMN_NAME_OBJECT_TYPE+"='prayer_rule_col'",null);
        Log.w("STARRED", String.valueOf(getPrayerCollection.toString()));
        String jsonArr;
        if (getPrayerCollection.moveToFirst()){
            do{
                jsonArr = getPrayerCollection.getString(2);
            }while (getPrayerCollection.moveToNext());
            Type prayersModelsListType = new TypeToken<List<PrayersModels>>(){}.getType();
            List<PrayersModels> prayersModelsTemp = new Gson().fromJson(jsonArr,prayersModelsListType);

            List<PrayersModels> filteredById = prayersModelsTemp.stream().filter((item)->item.getId() == id).collect(Collectors.toList());

            prayersModels.setValue(filteredById.get(0));
        }else {
            prayersModels = null;
        }
        getPrayerCollection.close();
        return prayersModels;
    }

    public void makingModel(int id, String name, String kafStart, String kafEnd, Integer prev, Integer next, List<PsalmModel> psalms) {
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

        PrayersModels prayersModel = new PrayersModels(id, name, tempHtml.toString(), prev, next);
        prayersCollection.add(prayersModel);
        mutablePrayersCollection.setValue(prayersCollection);
    }
}
