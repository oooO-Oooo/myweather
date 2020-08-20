package com.simple.myweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.ChineseCalendar;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.simple.myweather.gson.Forecast;
import com.simple.myweather.gson.Suggestion;
import com.simple.myweather.gson.Weather;
import com.simple.myweather.service.Api;
import com.simple.myweather.util.HttpUtil;
import com.simple.myweather.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "WeatherActivity";
    public static final String CITY_NAME = "city_name";
    public static final String WEATHER_ID = "weather_id";

    String countyName;
    String weatherId;

    ImageView mIconWeather;
    TextView mTempNow, mMaxMinTemp, mBodyFeel, mContentWeather;

    TextView mAqi, mPm25;

    TextView mMood, mSports, mCarWash;

    LinearLayout forecastLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mIconWeather = findViewById(R.id.icon_weather);
        mTempNow = findViewById(R.id.text_temp_now);
        mMaxMinTemp = findViewById(R.id.text_max_min_temp);
        mBodyFeel = findViewById(R.id.text_body_feel);
        mContentWeather = findViewById(R.id.text_weather);

        mAqi = findViewById(R.id.text_aqi_info);
        mPm25 = findViewById(R.id.text_pm25_info);

        mMood = findViewById(R.id.text_mood);
        mSports = findViewById(R.id.text_sports);
        mCarWash = findViewById(R.id.text_carWash);

        forecastLayout = findViewById(R.id.forecast);

        // 从单个页面获得数据
        Intent intent = getIntent();
        countyName = intent.getStringExtra(CITY_NAME);
        weatherId = intent.getStringExtra(WEATHER_ID);

        // 从SharedPreference读取数据，如果有就使用本地数据，如果没有就访问网络
        SharedPreferences editor = PreferenceManager.getDefaultSharedPreferences(this);
        String data = editor.getString("weather", null);
        if (data != null) {
            Weather weather = Utility.handleWeather(data);
            weatherInfo(weather);
        }else{
            dataFromServer(Api.API_KEY);
        }

    }

    private void dataFromServer(String key){
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
                final String res = response.body().string();
                final Weather weather = Utility.handleWeather(res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && weather.status.equals("ok")) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", res);
                            editor.apply();
                            weatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void weatherInfo(Weather weather) {

        String kTempNow, kBodyFeel, kContentWeather, kAqi, kPm25;
        String kMood, kSports, kCarWash;
        kTempNow = weather.now.getTmp();
        kBodyFeel = weather.suggestion.comfort.feel;
        kContentWeather = weather.now.getWeatherCondition();
        mTempNow.setText(kTempNow);
        mBodyFeel.setText(kBodyFeel);
        mContentWeather.setText(kContentWeather);
        kAqi = weather.aqi.city.aqi;
        kPm25 = weather.aqi.city.pm25;
        kMood = weather.suggestion.comfort.info;
        kSports = weather.suggestion.sport.info;
        kCarWash = weather.suggestion.carWash.info;

        if (kContentWeather.equals("晴")) {
            mIconWeather.setImageResource(R.drawable.ic_wb_sunny_black_24dp);
        } else if (kContentWeather.equals("阴") || kContentWeather.equals("多云")) {
            mIconWeather.setImageResource(R.drawable.ic_wb_cloudy_black_24dp);
        } else if (mContentWeather.equals("小雨")) {
            mIconWeather.setImageResource(R.drawable.ic_grain_black_24dp);
        } else {
            mIconWeather.setImageResource(R.drawable.ic_mood_black_24dp);
        }
        mAqi.setText(kAqi);
        mPm25.setText(kPm25);

        mMood.setText(kMood);
        mSports.setText(kSports);
        mCarWash.setText(kCarWash);

        forecastLayout.removeAllViews();
        for (int i = 0; i < weather.forecast.size(); i++) {
            Forecast forecast = weather.forecast.get(i);
            String kForecastDate, kForecastMax, kForecastMin, kWeather;
            View view = LayoutInflater.from(this).inflate(R.layout.one_day_forecast, forecastLayout, false);
            TextView mForecastDate, mForecastMax, mForecastMin;
            ImageView mForecastIcon;
            mForecastDate = view.findViewById(R.id.text_date);
            mForecastIcon = view.findViewById(R.id.icon_condition);
            mForecastMax = view.findViewById(R.id.text_max);
            mForecastMin = view.findViewById(R.id.text_min);
            kForecastDate = forecast.date;
            kForecastMax = forecast.tmp.max;
            kForecastMin = forecast.tmp.min;
            kWeather = forecast.conditon.weather;
            if (i == 0) {
                mForecastDate.setText("明天");
            } else {
                mForecastDate.setText(kForecastDate);
            }
            mForecastMax.setText(kForecastMax);
            mForecastMin.setText(kForecastMin);
            if (kWeather.equals("晴")) {
                mForecastIcon.setImageResource(R.drawable.ic_wb_sunny_black_24dp);
            } else if (kWeather.equals("阴") || kContentWeather.equals("多云")) {
                mForecastIcon.setImageResource(R.drawable.ic_wb_cloudy_black_24dp);
            } else if (kWeather.equals("小雨")) {
                mForecastIcon.setImageResource(R.drawable.ic_grain_black_24dp);
            } else {
                mForecastIcon.setImageResource(R.drawable.ic_mood_black_24dp);
            }
            forecastLayout.addView(view);
        }
    }
}
