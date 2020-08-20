package com.simple.myweather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    String date;
    WeatherCondition conditon;
    Tmp tmp;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    class WeatherCondition {
        @SerializedName("txt_d")
        String wether;

        public String getWether() {
            return wether;
        }

        public void setWether(String wether) {
            this.wether = wether;
        }
    }

    class Tmp {
        String max;
        String min;

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }
    }

}
