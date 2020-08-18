package com.simple.myweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("cond_txt")
    String weatherCondition;
    String tmp;
    @SerializedName("wind_dir")
    String windType;
}
