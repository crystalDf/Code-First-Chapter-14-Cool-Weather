package com.star.coolweather.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.star.coolweather.R;
import com.star.coolweather.db.CoolWeatherDB;
import com.star.coolweather.model.City;
import com.star.coolweather.model.Country;
import com.star.coolweather.model.Province;
import com.star.coolweather.util.HttpCallbackListener;
import com.star.coolweather.util.HttpUtil;
import com.star.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;

    private ProgressDialog mProgressDialog;

    private TextView mTitleTextView;
    private ListView mListView;

    private ArrayAdapter<String> mArrayAdapter;

    private static CoolWeatherDB sCoolWeatherDB;

    private List<String> mDataList = new ArrayList<>();

    private List<Province> mProvinceList;
    private List<City> mCityList;
    private List<Country> mCountryList;

    private Province mSelectedProvince;
    private City mSelectedCity;

    private int mCurrentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("weather_info",
                Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("city_selected", false)) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        getSupportActionBar().hide();

        setContentView(R.layout.choose_area);

        mListView = (ListView) findViewById(R.id.list_view);

        mTitleTextView = (TextView) findViewById(R.id.title);

        mArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, mDataList);

        mListView.setAdapter(mArrayAdapter);

        sCoolWeatherDB = CoolWeatherDB.getCoolWeatherDB(this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (mCurrentLevel) {
                    case LEVEL_PROVINCE:
                        mSelectedProvince = mProvinceList.get(position);
                        queryCities();
                        break;
                    case LEVEL_CITY:
                        mSelectedCity = mCityList.get(position);
                        queryCountries();
                        break;
                    case LEVEL_COUNTRY:
                        String countryCode = mCountryList.get(position).getCode();
                        Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                        intent.putExtra("country_code", countryCode);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        });

        queryProvinces();
    }

    private void queryProvinces() {
        mProvinceList = sCoolWeatherDB.loadProvinces();

        if (mProvinceList.size() > 0) {

            mDataList.clear();

            for (Province province : mProvinceList) {
                mDataList.add(province.getName());
            }

            mArrayAdapter.notifyDataSetChanged();

            mListView.setSelection(0);

            mTitleTextView.setText(getString(R.string.china));

            mCurrentLevel = LEVEL_PROVINCE;

        } else {
            queryFromServer(null, LEVEL_PROVINCE);
        }

    }

    private void queryCities() {
        mCityList = sCoolWeatherDB.loadCities(mSelectedProvince.getId());

        if (mCityList.size() > 0) {

            mDataList.clear();

            for (City city : mCityList) {
                mDataList.add(city.getName());
            }

            mArrayAdapter.notifyDataSetChanged();

            mListView.setSelection(0);

            mTitleTextView.setText(mSelectedProvince.getName());

            mCurrentLevel = LEVEL_CITY;

        } else {
            queryFromServer(mSelectedProvince.getCode(), LEVEL_CITY);
        }
    }

    private void queryCountries() {
        mCountryList = sCoolWeatherDB.loadCountries(mSelectedCity.getId());

        if (mCountryList.size() > 0) {

            mDataList.clear();

            for (Country country : mCountryList) {
                mDataList.add(country.getName());
            }

            mArrayAdapter.notifyDataSetChanged();

            mListView.setSelection(0);

            mTitleTextView.setText(mSelectedCity.getName());

            mCurrentLevel = LEVEL_COUNTRY;

        } else {
            queryFromServer(mSelectedCity.getCode(), LEVEL_COUNTRY);
        }
    }

    private void queryFromServer(final String code, final int level) {
        String address = "http://mobile.weather.com.cn/js/citylist.xml";

        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;

                switch (level) {
                    case LEVEL_PROVINCE:
                        result = Utility.handleProvincesResponse(sCoolWeatherDB, response);
                        break;
                    case LEVEL_CITY:
                        result = Utility.handleCitiesResponse(sCoolWeatherDB, response,
                                mSelectedProvince.getId());
                        break;
                    case LEVEL_COUNTRY:
                        result = Utility.handleCountriesResponse(sCoolWeatherDB, response,
                                mSelectedCity.getId());
                        break;
                }

                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();

                            switch (level) {
                                case LEVEL_PROVINCE:
                                    queryProvinces();
                                    break;
                                case LEVEL_CITY:
                                    queryCities();
                                    break;
                                case LEVEL_COUNTRY:
                                    queryCountries();
                                    break;
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();

                        Toast.makeText(ChooseAreaActivity.this, "loading failure",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrentLevel == LEVEL_COUNTRY) {
            queryCities();
        } else if (mCurrentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            if (getIntent().getBooleanExtra("from_weather_activity", false)) {
                Intent intent = new Intent(this, WeatherActivity.class);

                SharedPreferences.Editor editor = getSharedPreferences("weather_info",
                        Context.MODE_PRIVATE).edit();

                editor.putBoolean("city_selected", true).commit();

                startActivity(intent);
            }
            finish();
        }
    }
}
