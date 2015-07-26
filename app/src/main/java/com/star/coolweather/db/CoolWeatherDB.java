package com.star.coolweather.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.star.coolweather.model.City;
import com.star.coolweather.model.Country;
import com.star.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

public class CoolWeatherDB {

    public static final String DB_NAME = "cool_weather";

    public static final int VERSION = 1;

    private static CoolWeatherDB sCoolWeatherDB;

    private SQLiteDatabase mSQLiteDatabase;

    private CoolWeatherDB(Context context) {

        CoolWeatherOpenHelper coolWeatherOpenHelper = new CoolWeatherOpenHelper(
                context, DB_NAME, null, VERSION
        );

        mSQLiteDatabase = coolWeatherOpenHelper.getWritableDatabase();
    }

    public static CoolWeatherDB getCoolWeatherDB(Context context) {
        if (sCoolWeatherDB == null) {
            synchronized (CoolWeatherDB.class) {
                if (sCoolWeatherDB == null) {
                    sCoolWeatherDB = new CoolWeatherDB(context);
                }
            }
        }

        return sCoolWeatherDB;
    }

    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CoolWeatherOpenHelper.COLUMN_PROVINCE_NAME, province.getName());
            contentValues.put(CoolWeatherOpenHelper.COLUMN_PROVINCE_CODE, province.getCode());
            mSQLiteDatabase.insert(CoolWeatherOpenHelper.TABLE_PROVINCE_NAME, null, contentValues);
        }
    }

    public List<Province> loadProvinces() {
        List<Province> provinceList = new ArrayList<>();

        Cursor cursor = mSQLiteDatabase.query(CoolWeatherOpenHelper.TABLE_PROVINCE_NAME,
                null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Province province = new Province();

            province.setId(cursor.getInt(cursor.getColumnIndex(
                    CoolWeatherOpenHelper.COLUMN_PROVINCE_ID
            )));

            province.setName(cursor.getString(cursor.getColumnIndex(
                    CoolWeatherOpenHelper.COLUMN_PROVINCE_NAME
            )));

            province.setCode(cursor.getString(cursor.getColumnIndex(
                    CoolWeatherOpenHelper.COLUMN_PROVINCE_CODE
            )));

            provinceList.add(province);
        }

        if (cursor != null) {
            cursor.close();
        }

        return provinceList;
    }

    public String queryProvinceCode(int provinceId) {

        String provinceCode = null;

        Cursor cursor = mSQLiteDatabase.query(CoolWeatherOpenHelper.TABLE_PROVINCE_NAME,
                null,
                CoolWeatherOpenHelper.COLUMN_PROVINCE_ID + " = ?",
                new String[] {provinceId + ""},
                null, null, null);

        if (cursor.moveToFirst()) {
            provinceCode = cursor.getString(cursor.getColumnIndex(CoolWeatherOpenHelper.COLUMN_PROVINCE_CODE));
        }

        if (cursor != null) {
            cursor.close();
        }

        return provinceCode;
    }

    public void saveCity(City city) {
        if (city != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CoolWeatherOpenHelper.COLUMN_CITY_NAME, city.getName());
            contentValues.put(CoolWeatherOpenHelper.COLUMN_CITY_CODE, city.getCode());
            contentValues.put(CoolWeatherOpenHelper.COLUMN_CITY_PROVINCE_ID, city.getProvinceId());
            mSQLiteDatabase.insert(CoolWeatherOpenHelper.TABLE_CITY_NAME, null, contentValues);
        }
    }

    public List<City> loadCities(int provinceId) {
        List<City> cityList = new ArrayList<>();

        Cursor cursor = mSQLiteDatabase.query(CoolWeatherOpenHelper.TABLE_CITY_NAME,
                null,
                CoolWeatherOpenHelper.COLUMN_CITY_PROVINCE_ID + " = ?",
                new String[] {provinceId + ""},
                null, null, null);

        while (cursor.moveToNext()) {
            City city = new City();

            city.setId(cursor.getInt(cursor.getColumnIndex(
                    CoolWeatherOpenHelper.COLUMN_CITY_ID
            )));

            city.setName(cursor.getString(cursor.getColumnIndex(
                    CoolWeatherOpenHelper.COLUMN_CITY_NAME
            )));

            city.setCode(cursor.getString(cursor.getColumnIndex(
                    CoolWeatherOpenHelper.COLUMN_CITY_CODE
            )));

            city.setProvinceId(provinceId);

            cityList.add(city);
        }

        if (cursor != null) {
            cursor.close();
        }

        return cityList;
    }

    public String queryCityCode(int cityId) {

        String cityCode = null;

        Cursor cursor = mSQLiteDatabase.query(CoolWeatherOpenHelper.TABLE_CITY_NAME,
                null,
                CoolWeatherOpenHelper.COLUMN_CITY_ID + " = ?",
                new String[] {cityId + ""},
                null, null, null);

        if (cursor.moveToFirst()) {
            cityCode = cursor.getString(cursor.getColumnIndex(CoolWeatherOpenHelper.COLUMN_CITY_CODE));
        }

        if (cursor != null) {
            cursor.close();
        }

        return cityCode;
    }

    public void saveCountry(Country country) {
        if (country != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CoolWeatherOpenHelper.COLUMN_COUNTRY_NAME, country.getName());
            contentValues.put(CoolWeatherOpenHelper.COLUMN_COUNTRY_CODE, country.getCode());
            contentValues.put(CoolWeatherOpenHelper.COLUMN_COUNTRY_CITY_ID, country.getCityId());
            mSQLiteDatabase.insert(CoolWeatherOpenHelper.TABLE_COUNTRY_NAME, null, contentValues);
        }
    }

    public List<Country> loadCountries(int cityId) {
        List<Country> countryList = new ArrayList<>();

        Cursor cursor = mSQLiteDatabase.query(CoolWeatherOpenHelper.TABLE_COUNTRY_NAME,
                null,
                CoolWeatherOpenHelper.COLUMN_COUNTRY_CITY_ID + " = ?",
                new String[] {cityId + ""},
                null, null, null);

        while (cursor.moveToNext()) {
            Country country = new Country();

            country.setId(cursor.getInt(cursor.getColumnIndex(
                    CoolWeatherOpenHelper.COLUMN_COUNTRY_ID
            )));

            country.setName(cursor.getString(cursor.getColumnIndex(
                    CoolWeatherOpenHelper.COLUMN_COUNTRY_NAME
            )));

            country.setCode(cursor.getString(cursor.getColumnIndex(
                    CoolWeatherOpenHelper.COLUMN_COUNTRY_CODE
            )));

            country.setCityId(cityId);

            countryList.add(country);
        }

        if (cursor != null) {
            cursor.close();
        }

        return countryList;
    }

    public String queryCountryCode(int countryId) {

        String countryCode = null;

        Cursor cursor = mSQLiteDatabase.query(CoolWeatherOpenHelper.TABLE_COUNTRY_NAME,
                null,
                CoolWeatherOpenHelper.COLUMN_COUNTRY_ID + " = ?",
                new String[] {countryId + ""},
                null, null, null);

        if (cursor.moveToFirst()) {
            countryCode = cursor.getString(cursor.getColumnIndex(CoolWeatherOpenHelper.COLUMN_COUNTRY_CODE));
        }

        if (cursor != null) {
            cursor.close();
        }

        return countryCode;
    }
}
