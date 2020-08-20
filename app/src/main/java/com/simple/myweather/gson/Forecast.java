package com.simple.myweather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    public String date;
    public Tmp tmp;
    @SerializedName("cond")
    public WeatherCondition conditon;

    public class WeatherCondition {
        @SerializedName("txt_d")
        public String weather;
    }

    public class Tmp {
        public String max;
        public String min;
    }

}
