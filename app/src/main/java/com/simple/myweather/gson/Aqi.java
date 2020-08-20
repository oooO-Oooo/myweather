package com.simple.myweather.gson;

public class Aqi {
    public City city;

    public class City {
        public String aqi;
        public String pm25;
        public String qlty;
    }
}
