package com.star.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

import com.star.coolweather.receiver.AutoUpdateReceiver;
import com.star.coolweather.util.HttpCallbackListener;
import com.star.coolweather.util.HttpUtil;
import com.star.coolweather.util.Utility;


public class AutoUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();

                stopSelf();
            }
        }).start();

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

//        int period = 1000 * 60 * 60 * 8;
        int period = 1000 * 5;

        long triggerAtTime = SystemClock.elapsedRealtime() + period;

        Intent i = new Intent(this, AutoUpdateReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences sharedPreferences = getSharedPreferences("weather_info",
                Context.MODE_PRIVATE);

        String cityId = sharedPreferences.getString("city_id", "");

        String address = "http://www.weather.com.cn/data/cityinfo/" + cityId + ".html";

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this, response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
