package com.simple.myweather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    String date;
    WeatherCondition conditon;
    Tmp tmp;
    class WeatherCondition{
        @SerializedName("txt_d")
        String wether;
    }
    class Tmp{
        String max;
        String min;
    }}
