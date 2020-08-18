package com.simple.myweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public Aqi aqi;
    public Basic basic;
    public Forecast forecast;
    public Now now;
    @SerializedName("daily_forecast")
    public List<Suggestion> suggestions;
}
