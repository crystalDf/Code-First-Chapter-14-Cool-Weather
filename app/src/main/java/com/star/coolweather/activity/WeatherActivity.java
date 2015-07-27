package com.star.coolweather.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.coolweather.R;
import com.star.coolweather.service.AutoUpdateService;
import com.star.coolweather.util.HttpCallbackListener;
import com.star.coolweather.util.HttpUtil;
import com.star.coolweather.util.Utility;

public class WeatherActivity extends AppCompatActivity {

    private LinearLayout mWeatherLinearLayout;

    private TextView mCityNameTextView;

    private TextView mPublishTimeTextView;

    private TextView mWeatherDespTextView;

    private TextView mTemp1TextView;

    private TextView mTemp2TextView;

    private TextView mCurrentDateTextView;

    private Button mSwitchCityButton;

    private Button mRefreshWeatherButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.weather_layout);

        mWeatherLinearLayout = (LinearLayout) findViewById(R.id.weather_info_layout);

        mCityNameTextView = (TextView) findViewById(R.id.city_name);

        mPublishTimeTextView = (TextView) findViewById(R.id.publish_time);

        mWeatherDespTextView = (TextView) findViewById(R.id.weather_desp);

        mTemp1TextView = (TextView) findViewById(R.id.temp1);

        mTemp2TextView = (TextView) findViewById(R.id.temp2);

        mCurrentDateTextView = (TextView) findViewById(R.id.current_date);

        mSwitchCityButton = (Button) findViewById(R.id.switch_city);

        mRefreshWeatherButton = (Button) findViewById(R.id.refresh_weather);

        String countryCode = getIntent().getStringExtra("country_code");

        if (!TextUtils.isEmpty(countryCode)) {
            mPublishTimeTextView.setText("同步中...");
            mWeatherLinearLayout.setVisibility(View.INVISIBLE);
            mCityNameTextView.setVisibility(View.INVISIBLE);
            queryWeatherInfo("101" + countryCode);
        } else {
            showWeather();
        }

        mSwitchCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);

                SharedPreferences sharedPreferences = getSharedPreferences("weather_info",
                        Context.MODE_PRIVATE);

                sharedPreferences.edit().putBoolean("city_selected", false).commit();

                startActivity(intent);
                finish();
            }
        });

        mRefreshWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPublishTimeTextView.setText("同步中...");

                SharedPreferences sharedPreferences = getSharedPreferences("weather_info",
                        Context.MODE_PRIVATE);

                String cityId = sharedPreferences.getString("city_id", "");

                if (!TextUtils.isEmpty(cityId)) {
                    queryWeatherInfo(cityId);
                }
            }
        });
    }

    private void queryWeatherInfo(String cityId) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + cityId + ".html";
        queryFromServer(address);
    }

    private void queryFromServer(final String address) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(WeatherActivity.this, response);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPublishTimeTextView.setText("同步失败");
                    }
                });
            }
        });
    }

    public void showWeather() {
        SharedPreferences sharedPreferences = getSharedPreferences("weather_info",
                Context.MODE_PRIVATE);

        mCityNameTextView.setText(sharedPreferences.getString("city_name", ""));
        mTemp1TextView.setText(sharedPreferences.getString("temp1", ""));
        mTemp2TextView.setText(sharedPreferences.getString("temp2", ""));
        mWeatherDespTextView.setText(sharedPreferences.getString("weather", ""));
        mPublishTimeTextView.setText("今天" + sharedPreferences.getString("p_time", "") + "发布");
        mCurrentDateTextView.setText(sharedPreferences.getString("current_date", ""));

        mWeatherLinearLayout.setVisibility(View.VISIBLE);
        mCityNameTextView.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

    }
}
