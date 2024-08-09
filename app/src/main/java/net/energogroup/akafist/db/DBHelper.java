package net.energogroup.akafist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "Akafist.db";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(StarredDTO.SQL_CREATE_STARRED);
        sqLiteDatabase.execSQL(PrayersDTO.SQL_CREATE_PRAYERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(StarredDTO.SQL_DELETE_STARRED);
        sqLiteDatabase.execSQL(PrayersDTO.SQL_DELETE_PRAYERS);
        onCreate(sqLiteDatabase);
    }
}
