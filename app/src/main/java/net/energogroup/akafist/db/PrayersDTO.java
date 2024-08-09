package net.energogroup.akafist.db;

public class PrayersDTO {
    public static final String TABLE_NAME = "prayers";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_TEXT = "text";
    public static final String COLUMN_NAME_PREV = "prev";
    public static final String COLUMN_NAME_NEXT = "next";

    public static final String SQL_CREATE_PRAYERS =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_NAME_NAME + " TEXT, " +
                    COLUMN_NAME_TEXT + " TEXT, " +
                    COLUMN_NAME_PREV + " INTEGER, " +
                    COLUMN_NAME_NEXT + " INTEGER)";

    public static final String SQL_DELETE_PRAYERS =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
