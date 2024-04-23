package com.example.weatherapp.Model;

public class WeatherRecyclerViewModel {

    String time,temperature,icon,windspeed;
    int is_day;

    public WeatherRecyclerViewModel(String time, String temperature, String icon, String windspeed,int day) {
        this.time = time;
        this.temperature = temperature;
        this.icon = icon;
        this.windspeed = windspeed;
        this.is_day=day;
    }

    public int getIs_day() {
        return is_day;
    }

    public void setIs_day(int is_day) {
        this.is_day = is_day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(String windspeed) {
        this.windspeed = windspeed;
    }
}