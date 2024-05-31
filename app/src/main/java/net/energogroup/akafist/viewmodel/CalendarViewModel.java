package net.energogroup.akafist.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.Response;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.models.ServicesModel;
import net.energogroup.akafist.models.TypesModel;
import net.energogroup.akafist.models.azbykaAPI.WholeDay;
import net.energogroup.akafist.models.azbykaAPI.WholeDayText;
import net.energogroup.akafist.models.azbykaAPI.WholeDayTropariaOrKontakia;
import net.energogroup.akafist.service.RequestServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CalendarViewModel extends ViewModel {

    private final List<TypesModel> typesModelList = new ArrayList<>();
    private final List<ServicesModel> servicesModelList = new ArrayList<>();
    private final List<WholeDay> wholeDaysList = new ArrayList<>();
    private final MutableLiveData<List<ServicesModel>> mutableServicesList = new MutableLiveData<>();
    private final MutableLiveData<List<TypesModel>> mutableTypesList = new MutableLiveData<>();
    private final MutableLiveData<String> liveDataTxt = new MutableLiveData<>();
    private final MutableLiveData <String> liveNameTxt = new MutableLiveData<>();
    private final MutableLiveData <String> liveDate = new MutableLiveData<>();
    private final MutableLiveData<List<WholeDay>> wholeDays = new MutableLiveData<>();
    private final MutableLiveData<List<WholeDayText>> wholeDayTexts = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFinished = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isFinishedTexts = new MutableLiveData<>(false);

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

    public MutableLiveData<String> getLiveDate() { return liveDate; }

    public MutableLiveData<List<WholeDay>> getWholeDays() {
        return wholeDays;
    }

    public MutableLiveData<Boolean> getIsFinished() {
        return isFinished;
    }

    public MutableLiveData<Boolean> getIsFinishedTexts() { return isFinishedTexts; }

    public void getByDate(String date, Context context){
        isFinishedTexts.setValue(false);
        isFinished.setValue(false);
        wholeDaysList.clear();
        String urlToGet = context.getResources().getString(MainActivity.AZBYKA_API_PATH)+"day?date[exact]="+date;

        RequestServiceHandler serviceHandler = new RequestServiceHandler();
        serviceHandler.addHeader("Content-Type","Application/json");
        serviceHandler.addHeader("Connection", "keep-alive");
        serviceHandler.addHeader("Authorization", "Bearer "+context.getResources().getString(MainActivity.AZBYKA_TOKEN));

        serviceHandler.objectRequest(urlToGet, Request.Method.GET,
                null, JSONArray.class,
                (Response.Listener<JSONArray>) response -> {
                    JSONObject jsonObject, abstractDate;
                    JSONArray priorities;
                    String  memorialDayTitle;
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            jsonObject = response.getJSONObject(i);
                            abstractDate = jsonObject.getJSONObject("abstractDate");
                            int j = 0;
                            priorities = abstractDate.getJSONArray("priorities");
                            if(priorities.length() != 0) {
                                WholeDay wholeDay = new WholeDay();
                                ArrayList<String> memorialDayTitles = new ArrayList<>();
                                while (j < priorities.length()) {
                                    memorialDayTitle = priorities.getJSONObject(j).getJSONObject("memorialDay").getString("cacheTitle");
                                    memorialDayTitles.add(memorialDayTitle);
                                    j++;
                                }
                                wholeDay.setMemorialDays(memorialDayTitles);

                                DateTimeFormatter formatterToDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", new Locale("ru"));
                                LocalDate localDate = LocalDate.parse(date, formatterToDate);
                                DateTimeFormatter formatterToStr = DateTimeFormatter.ofPattern("dd MMMM, EEEE", new Locale("ru"));
                                String formatedDate = localDate.format(formatterToStr);
                                wholeDay.setDate(formatedDate);

                                wholeDaysList.add(wholeDay);
                                Log.e("RESPONSE", wholeDay.toString());
                                wholeDays.setValue(wholeDaysList);
                                getTextsByDate(date, context, wholeDay);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("Response", error.getMessage()));
    }

    public void getTextsByDate(String date, Context context, WholeDay wholeDay){
        String urlToGet = context.getResources().getString(MainActivity.AZBYKA_API_PATH)+"cache_dates?date[exact]="+date;

        RequestServiceHandler serviceHandler = new RequestServiceHandler();
        serviceHandler.addHeader("Content-Type","Application/json");
        serviceHandler.addHeader("Connection", "keep-alive");
        serviceHandler.addHeader("Authorization", "Bearer "+context.getResources().getString(MainActivity.AZBYKA_TOKEN));

        serviceHandler.objectRequest(urlToGet, Request.Method.GET,
                null, JSONArray.class,
                (Response.Listener<JSONArray>) response -> {
                    JSONObject jsonObject, abstractDate;
                    JSONArray texts, tropariaOrKontakia;
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            jsonObject = response.getJSONObject(i);
                            abstractDate = jsonObject.getJSONObject("abstractDate");
                            int j = 0;
                            texts = abstractDate.getJSONArray("texts");
                            Log.e("RESPONSE", String.valueOf(texts));
                            if(texts.length() != 0) {
                                ArrayList<WholeDayText> textsArr = new ArrayList<>();
                                while (j < texts.length()) {
                                    WholeDayText text = new WholeDayText(
                                            texts.getJSONObject(j).getInt("id"),
                                            texts.getJSONObject(j).getInt("type"));
                                    textsArr.add(text);
                                    j++;
                                }
                                List<WholeDayText> readToday = textsArr.stream().filter(wholeDayText -> wholeDayText.getType() == 1).collect(Collectors.toList());
                                if(!readToday.isEmpty()) {
                                    for (WholeDayText today : readToday) {
                                        getReadToday(today, context);
                                    }
                                }
                                wholeDay.setTexts(textsArr);
                            }
                            j=0;
                            tropariaOrKontakia = abstractDate.getJSONArray("tropariaOrKontakia");
                            if(tropariaOrKontakia.length() != 0) {
                                ArrayList<WholeDayTropariaOrKontakia> tropariaOrKontakias = new ArrayList<>();
                                while (j < tropariaOrKontakia.length()) {
                                    WholeDayTropariaOrKontakia wholeDayTropariaOrKontakia = new WholeDayTropariaOrKontakia(
                                            tropariaOrKontakia.getJSONObject(j).getInt("id"));
                                    tropariaOrKontakias.add(wholeDayTropariaOrKontakia);
                                    j++;
                                }
                                wholeDay.setTropariaOrKontakias(tropariaOrKontakias);
                            }
                        }
                        isFinished.setValue(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("Response", error.getMessage()));
    }

    public void getReadToday(WholeDayText readToday, Context context){
        String urlToGet = context.getResources().getString(MainActivity.AZBYKA_API_PATH)+"texts/"+readToday.getId();

        RequestServiceHandler serviceHandler = new RequestServiceHandler();
        serviceHandler.addHeader("Content-Type","Application/json");
        serviceHandler.addHeader("Connection", "keep-alive");
        serviceHandler.addHeader("Authorization", "Bearer "+context.getResources().getString(MainActivity.AZBYKA_TOKEN));

        serviceHandler.objectRequest(urlToGet, Request.Method.GET,
                null, JSONObject.class,
                (Response.Listener<JSONObject>) response -> {
                    try {
                        readToday.setText(response.getString("text"));
                        isFinishedTexts.setValue(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("Response", error.getMessage()));
    }
}
