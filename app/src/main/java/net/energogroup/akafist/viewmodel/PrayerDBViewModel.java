package net.energogroup.akafist.viewmodel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.OptIn;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.util.UnstableApi;

import net.energogroup.akafist.db.PrayersDTO;
import net.energogroup.akafist.models.PrayersModels;

public class PrayerDBViewModel extends ViewModel {

    private static final String TAG = "PRAYER_DB_VIEW_MODEL";
    private final MutableLiveData<PrayersModels> prayersModelMLD = new MutableLiveData<>();

    public MutableLiveData<PrayersModels> getPrayersModelMLD() {
        return prayersModelMLD;
    }

    @OptIn(markerClass = UnstableApi.class)
    public boolean checkPrayerInDB(SQLiteDatabase db, String prevMenu, int id) {
        Cursor cursorPrayers = db.rawQuery("SELECT * FROM " + PrayersDTO.TABLE_NAME + " WHERE "
                + PrayersDTO.COLUMN_NAME_ID + "="+ id, null);

        boolean result = false; //false by default

        if(cursorPrayers.moveToFirst()) {
            do{
                result = true;
                PrayersModels prayersModels = new PrayersModels();
                prayersModels.setId(Integer.parseInt(cursorPrayers.getString(0)));
                prayersModels.setName(cursorPrayers.getString(1).split("/")[1]);
                prayersModels.setHtml(cursorPrayers.getString(2));
                prayersModels.setPrev(Integer.parseInt(cursorPrayers.getString(3)));
                prayersModels.setNext(Integer.parseInt(cursorPrayers.getString(4)));
                prayersModelMLD.setValue(prayersModels);
            }while(cursorPrayers.moveToNext());
        }
        cursorPrayers.close();
        return result;
    }
}
