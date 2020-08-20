package com.simple.myweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("cond_txt")
    String weatherCondition;
    String tmp;

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getWindType() {
        return windType;
    }

    public void setWindType(String windType) {
        this.windType = windType;
    }

    @SerializedName("wind_dir")
    String windType;
}
