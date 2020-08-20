package com.simple.myweather.gson;

public class Aqi {
    public City city;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    class City {
        String aqi;
        String pm25;
        String qlty;
    }
}
