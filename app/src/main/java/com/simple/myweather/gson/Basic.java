package com.simple.myweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("cid")
    public String WeatherId;

    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;

    }
}
