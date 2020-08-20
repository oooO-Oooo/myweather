package com.simple.myweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.simple.myweather.gson.Suggestion;
import com.simple.myweather.gson.Weather;
import com.simple.myweather.service.Api;
import com.simple.myweather.util.HttpUtil;
import com.simple.myweather.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "WeatherActivity";
    public static final String CITY_NAME = "city_name";
    public static final String WEATHER_ID = "weather_id";

    String countyName;
    String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // 从闪个页面获得数据
        Intent intent = getIntent();
        countyName = intent.getStringExtra(CITY_NAME);
        weatherId = intent.getStringExtra(WEATHER_ID);



        String key = "491f41bf0e8040a9ab2a5188f8d4406f";
        String addr = Api.IP + "weather?cityid=" + weatherId + "&key=" + key;
        HttpUtil.sendOKHttpRequest(addr, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "Problem", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                final Weather weather = Utility.handleWeather(res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weather
                    }
                });
            }
        });
    }

    private void weatherInfo(Weather weather) {

    }
}
