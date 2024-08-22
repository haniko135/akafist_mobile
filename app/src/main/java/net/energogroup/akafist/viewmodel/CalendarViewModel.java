package net.energogroup.akafist.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.api.AzbykaAPI;
import net.energogroup.akafist.models.azbykaAPI.AbstractDate;
import net.energogroup.akafist.models.azbykaAPI.LoginCredentials;
import net.energogroup.akafist.models.azbykaAPI.WholeDayText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CalendarViewModel extends ViewModel {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final List<AbstractDate> wholeDaysList = new ArrayList<>();
    private final AbstractDate wholeDay = new AbstractDate();
    private final MutableLiveData<List<AbstractDate>> wholeDays = new MutableLiveData<>();
    private final MutableLiveData<List<WholeDayText>> wholeDayTexts = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFinished = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isFinishedTexts = new MutableLiveData<>(false);
    private SharedPreferences preferences;

    public MutableLiveData<List<AbstractDate>> getWholeDays() {
        return wholeDays;
    }

    public MutableLiveData<Boolean> getIsFinished() {
        return isFinished;
    }

    public MutableLiveData<Boolean> getIsFinishedTexts() { return isFinishedTexts; }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public void initialize(Context context){
        preferences = context.getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void getByDate(AzbykaAPI azbykaAPI, String date, Context context){
        isFinishedTexts.setValue(false);
        isFinished.setValue(false);
        wholeDaysList.clear();

        compositeDisposable.add(
                azbykaAPI.getAbstractDates(date)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(success->{
                            if(success.code() == 200) {
                                //AbstractDate wholeDay = new AbstractDate();
                                success.body().forEach(successBody -> {
                                    Log.e("RESPONSE", successBody.getAbstractDate().getPriorities().toString());
                                    ArrayList<String> memorialDayTitles = new ArrayList<>();
                                    successBody.getAbstractDate().getPriorities().forEach(priority -> {
                                        memorialDayTitles.add(priority.getMemorialDay().getCacheTitle());
                                        wholeDay.setMemorialDays(memorialDayTitles);
                                    });

                                    DateTimeFormatter formatterToDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", new Locale("ru"));
                                    LocalDate localDate = LocalDate.parse(date, formatterToDate);
                                    DateTimeFormatter formatterToStr = DateTimeFormatter.ofPattern("dd MMMM, EEEE", new Locale("ru"));
                                    String formatedDate = localDate.format(formatterToStr);
                                    wholeDay.setDate(formatedDate);

                                    wholeDaysList.add(wholeDay);
                                    wholeDays.setValue(wholeDaysList);
                                });
                                getTextsByDate(azbykaAPI, date, context, wholeDay);
                            } else if (success.code() == 401){
                                Map<String, Object> params = new HashMap<>();
                                params.put("date", date);
                                loginToAzbykaApi(azbykaAPI, "getByDate", params,  context);
                            } else if (success.code() == 404) {
                                Log.e("Response", success.headers().toString());
                                Log.e("Response", success.errorBody().toString());
                            }
                        }, error -> {
                            Log.e("Response", error.getMessage());
                        })
        );
    }

    public void loginToAzbykaApi(AzbykaAPI azbykaAPI, String pastRequest, Map<String, Object> pastRequestParams, Context context){
        LoginCredentials loginCredentials = new LoginCredentials(
                context.getResources().getString(R.string.azbykaApiLogin),
                context.getResources().getString(R.string.azbykaApiPassword)
        );

        compositeDisposable.add(
                azbykaAPI.login(loginCredentials)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(success->{
                            if (success.code() == 200){
                                String azbykaToken = success.body().getToken();
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("app_pref_azbyka_token", azbykaToken);
                                editor.apply();

                                if(Objects.equals(pastRequest, "getByDate")){
                                    //getByDate(pastRequestParams.get("date").toString(), context);
                                    getByDate(azbykaAPI, pastRequestParams.get("date").toString(), context);
                                } else if (Objects.equals(pastRequest, "getTextsByDate")){
                                    getTextsByDate(azbykaAPI, pastRequestParams.get("date").toString(), context, (AbstractDate) pastRequestParams.get("wholeDay"));
                                } else if (Objects.equals(pastRequest, "getReadToday")){
                                    getReadToday(azbykaAPI,(WholeDayText) pastRequestParams.get("readToday"), context);
                                }
                            } else if (success.code() == 404) {
                                Log.e("Respone", "ну и херня");
                            }
                        }, error -> {
                            Log.e("Response", error.getMessage());
                        })
        );
    }

    public void getTextsByDate(AzbykaAPI azbykaAPI, String date, Context context, AbstractDate wholeDay){
        compositeDisposable.add(
                azbykaAPI.getDate(date)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(success->{
                            if (success.code() == 200){
                                success.body().forEach(successBody->{
                                    if (successBody.getAbstractDate().getTexts() != null && !successBody.getAbstractDate().getTexts().isEmpty()) {
                                        ArrayList<WholeDayText> textsArr = new ArrayList<>(successBody.getAbstractDate().getTexts());
                                        Log.e("RESPONSE 4", String.valueOf(successBody.getAbstractDate().getTexts()));

                                        List<WholeDayText> readToday = textsArr.stream().filter(wholeDayText -> wholeDayText.getType() == 1).collect(Collectors.toList());
                                        if (!readToday.isEmpty()) {
                                            for (WholeDayText today : readToday) {
                                                getReadToday(azbykaAPI, today, context);
                                            }
                                        }
                                        wholeDay.setTexts(textsArr);
                                    }

                                    wholeDay.setTropariaOrKontakias(successBody.getAbstractDate().getTropariaOrKontakias());
                                });
                                isFinished.setValue(true);
                            } else if (success.code() == 401) {
                                Map<String, Object> params = new HashMap<>();
                                params.put("date", date);
                                params.put("wholeDay", wholeDay);
                                loginToAzbykaApi(azbykaAPI, "getTextsByDate", params, context);
                            }
                        }, error -> {
                            Log.e("Response", error.getMessage());
                        })
        );
    }

    public void getReadToday(AzbykaAPI azbykaAPI, WholeDayText readToday, Context context){
        compositeDisposable.add(
                azbykaAPI.getText(readToday.getId())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(success->{
                            if (success.code() == 200){
                                readToday.setText(success.body().getText());
                                isFinishedTexts.setValue(true);
                            } else if (success.code() == 401) {
                                Map<String, Object> params = new HashMap<>();
                                params.put("readToday", readToday);
                                loginToAzbykaApi(azbykaAPI, "getReadToday", params, context);
                            }
                        },error -> {
                            Log.e("Response", error.getMessage());
                        })
        );
    }
}
