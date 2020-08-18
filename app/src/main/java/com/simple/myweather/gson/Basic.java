package com.simple.myweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("city")
    String cityName;
    @SerializedName("cid")
    String WeatherId;

    Update update;
    public class Update{
        @SerializedName("loc")
        String updateTime;
    }
}
