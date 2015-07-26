package com.star.coolweather.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {

    public static void sendHttpRequest(final String address,
                                       final HttpCallbackListener httpCallbackListener) {

        if (!isNetworkAvailable()) {
            Toast.makeText(MyApplication.getContext(), "network is unavailable",
                    Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;

                try {
                    URL url = new URL(address);

                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(8000);
                    httpURLConnection.setReadTimeout(8000);

                    InputStream inputStream = httpURLConnection.getInputStream();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                            inputStream
                    ));

                    StringBuilder stringBuilder = new StringBuilder();

                    String line = null;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    if (httpCallbackListener != null) {
                        httpCallbackListener.onFinish(stringBuilder.toString());
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    if (httpCallbackListener != null) {
                        httpCallbackListener.onError(e);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (httpCallbackListener != null) {
                        httpCallbackListener.onError(e);
                    }
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    private static boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
