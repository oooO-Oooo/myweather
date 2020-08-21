package com.simple.myweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.ChineseCalendar;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.simple.myweather.gson.Forecast;
import com.simple.myweather.gson.Suggestion;
import com.simple.myweather.gson.Weather;
import com.simple.myweather.service.Api;
import com.simple.myweather.util.DateToWeek;
import com.simple.myweather.util.HttpUtil;
import com.simple.myweather.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;

public class WeatherActivity extends AppCompatActivity{

    private static final String TAG = "WeatherActivity";
    public static final String CITY_NAME = "city_name";
    public static final String WEATHER_ID = "weather_id";

    public String countyName;
    public String weatherId;

    ImageView backImg;
    public DrawerLayout drawerLayout;
    Button switchBtn;
    ImageView mIconWeather;
    TextView mTempNow, mMaxMinTemp, mBodyFeel, mContentWeather, mTitle;

    TextView mAqi, mPm25;

    TextView mMood, mSports, mCarWash;

    LinearLayout forecastLayout;

    public SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        if (Build.VERSION.SDK_INT>=21){
            View view = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            view.setSystemUiVisibility(uiOptions);
        }

        switchBtn = findViewById(R.id.switch_btn);

        drawerLayout = findViewById(R.id.drawer_layout);

        mIconWeather = findViewById(R.id.icon_weather);
        mTempNow = findViewById(R.id.text_temp_now);
        mMaxMinTemp = findViewById(R.id.text_max_min_temp);
        mBodyFeel = findViewById(R.id.text_body_feel);
        mContentWeather = findViewById(R.id.text_weather);
        mTitle = findViewById(R.id.title_name);

        mAqi = findViewById(R.id.text_aqi_info);
        mPm25 = findViewById(R.id.text_pm25_info);

        mMood = findViewById(R.id.text_mood);
        mSports = findViewById(R.id.text_sports);
        mCarWash = findViewById(R.id.text_carWash);
        refreshLayout = findViewById(R.id.swip_refresh);
        forecastLayout = findViewById(R.id.forecast);
        backImg = findViewById(R.id.back_ground);

        refreshLayout.setColorSchemeColors(Color.RED,Color.GREEN,Color.YELLOW);

        //打开抽屉
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // 从SharedPreference读取数据，如果有就使用本地数据，如果没有就访问网络
        SharedPreferences editor = PreferenceManager.getDefaultSharedPreferences(this);
        String data = editor.getString("weather", null);
        String imgUrl = editor.getString("imgUrl",null);
        countyName = editor.getString(CITY_NAME,null);
        weatherId = editor.getString(WEATHER_ID,null);

        if (data != null) {
            Weather weather = Utility.handleWeather(data);
            weatherInfo(weather);
        } else {
            dataFromServer();
        }

        // 加载背景图片
        if (imgUrl != null){
            Glide.with(this).load(imgUrl).into(backImg);
        }else{
            loadImgFromServer();
        }
        mTitle.setText(countyName);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataFromServer();
            }
        });
    }

    // 从服务器加载背景图片
    private void loadImgFromServer() {
        HttpUtil.sendOKHttpRequest(Api.BACKGROUND_IMG_URL, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "图片加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String res= response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("imgUrl",res);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(res).into(backImg);
                    }
                });
            }
        });
    }

    //从服务器加载数据
    public void dataFromServer() {
        String addr = Api.IP + "weather?cityid=" + weatherId + "&key=" + Api.API_KEY;
        HttpUtil.sendOKHttpRequest(addr, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "Problem", Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(WeatherActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
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
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(WeatherActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        loadImgFromServer();
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
        mTitle.setText(countyName);

        forecastLayout.removeAllViews();
        for (int i = 0; i < weather.forecast.size(); i++) {
            Forecast forecast = weather.forecast.get(i);
            String kForecastDate, kForecastMax, kForecastMin, kWeather,weekDay = null;
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

            //将日期转换为星期
            try {
                weekDay = DateToWeek.dayForWeek(kForecastDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (i == 0) {
                mForecastDate.setText("明天");
            } else {
                mForecastDate.setText(weekDay);
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
