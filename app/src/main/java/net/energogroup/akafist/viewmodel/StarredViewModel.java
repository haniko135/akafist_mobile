package net.energogroup.akafist.viewmodel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.db.StarredDTO;
import net.energogroup.akafist.models.ServicesModel;
import net.energogroup.akafist.models.StarredModel;

import java.util.ArrayList;
import java.util.List;

public class StarredViewModel extends ViewModel {
    private MutableLiveData<List<ServicesModel>> textPrayers = new MutableLiveData<>();

    public MutableLiveData<List<ServicesModel>> getTextPrayers() { return textPrayers; }

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
}
