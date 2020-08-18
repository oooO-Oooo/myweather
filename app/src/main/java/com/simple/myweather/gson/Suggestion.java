package com.simple.myweather.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {

    Comfort comfort;
    CarWash carWash;
    Sport sport;

    public class Comfort{
        @SerializedName("brf")
        String feel;
        @SerializedName("txt")
        String suggest;
    }
    public class CarWash{
        @SerializedName("brf")
        String feel;
        @SerializedName("txt")
        String suggest;
    }
    public class Sport{
        @SerializedName("brf")
        String feel;
        @SerializedName("txt")
        String suggest;
    }
}
