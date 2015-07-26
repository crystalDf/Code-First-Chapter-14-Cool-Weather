package com.star.coolweather.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

    public static final String TABLE_PROVINCE_NAME = "Province";
    public static final String TABLE_CITY_NAME = "City";
    public static final String TABLE_COUNTRY_NAME = "Country";

    public static final String COLUMN_PROVINCE_ID = "id";
    public static final String COLUMN_PROVINCE_NAME = "name";
    public static final String COLUMN_PROVINCE_CODE = "code";

    public static final String COLUMN_CITY_ID = "id";
    public static final String COLUMN_CITY_NAME = "name";
    public static final String COLUMN_CITY_CODE = "code";
    public static final String COLUMN_CITY_PROVINCE_ID = "province_id";

    public static final String COLUMN_COUNTRY_ID = "id";
    public static final String COLUMN_COUNTRY_NAME = "name";
    public static final String COLUMN_COUNTRY_CODE = "code";
    public static final String COLUMN_COUNTRY_CITY_ID = "city_id";

    public static final String CREATE_TABLE_PROVINCE = "CREATE TABLE " + TABLE_PROVINCE_NAME +
            " ( " +
            COLUMN_PROVINCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PROVINCE_NAME + " TEXT, " +
            COLUMN_PROVINCE_CODE + " TEXT" + ")";

    public static final String CREATE_TABLE_CITY = "CREATE TABLE " + TABLE_CITY_NAME +
            " ( " +
            COLUMN_CITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CITY_NAME + " TEXT, " +
            COLUMN_CITY_CODE + " TEXT, " +
            COLUMN_CITY_PROVINCE_ID + " INTEGER" + ")";

    public static final String CREATE_TABLE_COUNTRY = "CREATE TABLE " + TABLE_COUNTRY_NAME +
            " ( " +
            COLUMN_COUNTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_COUNTRY_NAME + " TEXT, " +
            COLUMN_COUNTRY_CODE + " TEXT, " +
            COLUMN_COUNTRY_CITY_ID + " INTEGER" + ")";

    public CoolWeatherOpenHelper(Context context, String name,
                                 SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PROVINCE);
        db.execSQL(CREATE_TABLE_CITY);
        db.execSQL(CREATE_TABLE_COUNTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
