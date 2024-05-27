package net.energogroup.akafist.viewmodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.db.StarredDTO;
import net.energogroup.akafist.models.PrayersModels;
import net.energogroup.akafist.models.ServicesModel;
import net.energogroup.akafist.models.StarredModel;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StarredViewModel extends ViewModel {
    private MutableLiveData<List<ServicesModel>> textPrayers = new MutableLiveData<>();
    private MutableLiveData<List<ServicesModel>> prayerRules = new MutableLiveData<>();
    private List<PrayersModels> prayersCollection = new ArrayList<>();
    private MutableLiveData<List<PrayersModels>> mutablePrayersCollection = new MutableLiveData<>();
    private MutableLiveData<PrayersModels> prayersModels = new MutableLiveData<>();
    private MutableLiveData<Boolean> isConverted = new MutableLiveData<>(false);

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
        prayerRuleText.forEach((item)->{
            tempStrings.add(gson1.toJson(item));
        });
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
        String stringArr = "";
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

    public void convertToPrayersModels(Context context, SQLiteDatabase db, LifecycleOwner lifecycleOwner){
        isConverted.setValue(false);
        String[] selectionArgs = { "prayer_rule_col" };

        db.delete(StarredDTO.TABLE_NAME,StarredDTO.COLUMN_NAME_OBJECT_TYPE + " LIKE ?",
                selectionArgs);

        List<ServicesModel> tempModels = prayerRules.getValue();
        for (int i = 0; i < tempModels.size(); i++) {
            if(i == 0){
                getJsonSaved(tempModels.get(i).getDate(),
                        tempModels.get(i).getId(), context,
                        tempModels.get(i+1).getId(), 0);
            } else if (i == tempModels.size()-1) {
                getJsonSaved(tempModels.get(i).getDate(),
                        tempModels.get(i).getId(), context,
                        0, tempModels.get(i-1).getId());
            }else {
                getJsonSaved(tempModels.get(i).getDate(),
                        tempModels.get(i).getId(), context,
                        tempModels.get(i+1).getId(),
                        tempModels.get(i-1).getId());
            }
        }

        mutablePrayersCollection.observe(lifecycleOwner, prayersModels -> {
            if(prayersModels.size() == tempModels.size()) {

                Gson gson = new Gson();
                String jsonString = gson.toJson(prayersModels);

                ContentValues contentValues = new ContentValues();
                contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_URL, jsonString);
                contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_TYPE, "prayer_rule_col");
                contentValues.put(StarredDTO.COLUMN_NAME_ID, Math.round(Math.random() * 1000));
                db.insert(StarredDTO.TABLE_NAME, null, contentValues);
                isConverted.setValue(true);
            }
        });
    }

    public void getJsonSaved(String date, int id, Context context, int nextId, int prevId){
        String urlToGet = context.getString(MainActivity.API_PATH)+date+"/"+id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                urlToGet, null, response -> {
            int idNew, prev, next;
            String  name, html;
            try {
                idNew = id;
                name = StringEscapeUtils.unescapeJava(response.getString("name"));
                html = StringEscapeUtils.unescapeJava(response.getString("html"));
                prev = prevId;
                next = nextId;

                prayersCollection.add(new PrayersModels(idNew,name,html,prev,next));
                mutablePrayersCollection.setValue(prayersCollection);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", context.getString(MainActivity.APP_VER));
                headers.put("Connection", "keep-alive");
                return headers;
            }

        };
        MainActivity.mRequestQueue.add(request);
    }

    public MutableLiveData<PrayersModels> getPrayerModelsCollectionItem(int id, SQLiteDatabase db){
        Cursor getPrayerCollection = db.rawQuery("SELECT * FROM "+StarredDTO.TABLE_NAME+" WHERE "+StarredDTO.COLUMN_NAME_OBJECT_TYPE+"='prayer_rule_col'",null);
        String jsonArr = "";
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
}
