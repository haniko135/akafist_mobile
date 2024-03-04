package net.energogroup.akafist.db;

public class StarredDTO {
    public static final String TABLE_NAME = "starred";
    public static final String COLUMN_NAME_ID = "ID";
    public static final String COLUMN_NAME_OBJECT_TYPE = "object_type";
    public static final String COLUMN_NAME_OBJECT_URL = "object_url";

    public static final String SQL_CREATE_STARRED =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_OBJECT_TYPE + " TEXT," +
                    COLUMN_NAME_OBJECT_URL + " TEXT)";

    public static final String SQL_DELETE_STARRED =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
