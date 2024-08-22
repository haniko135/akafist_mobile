package net.energogroup.akafist.viewmodel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.db.PrayersDTO;
import net.energogroup.akafist.models.ServicesModel;

import java.util.ArrayList;
import java.util.List;

public class OfflinePrayerViewModel extends ViewModel {
    private final MutableLiveData<List<ServicesModel>> prayersDB = new MutableLiveData<>();

    public MutableLiveData<List<ServicesModel>> getPrayersDB() {
        return prayersDB;
    }

    public void getDownloadedPrayers(Fragment fragment){
        SQLiteDatabase db = ((MainActivity)fragment.getActivity()).getDbHelper().getWritableDatabase();
        Cursor allPrayers = db.rawQuery("SELECT * FROM "+ PrayersDTO.TABLE_NAME, null);

        ArrayList<ServicesModel> prayers = new ArrayList<>();
        if(allPrayers.moveToFirst()){
            do {
                ServicesModel prayer = new ServicesModel(
                        Integer.parseInt(allPrayers.getString(0)),
                        allPrayers.getString(1),
                        0,
                        null
                );
                prayers.add(prayer);
            }while (allPrayers.moveToNext());
        }else {
            prayers = null;
        }
        prayersDB.setValue(prayers);
    }

}
