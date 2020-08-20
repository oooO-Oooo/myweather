package com.simple.myweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public String status;
    public Aqi aqi;
    public Now now;
    public Basic basic;
    @SerializedName("daily_forecast")
    public List<Forecast> forecast;
    public Suggestion suggestion;
}
