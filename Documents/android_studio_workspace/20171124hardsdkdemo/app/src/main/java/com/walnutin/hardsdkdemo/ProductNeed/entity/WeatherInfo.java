package com.walnutin.hardsdkdemo.ProductNeed.entity;

import java.io.Serializable;

/**
 * 作者：MrJiang on 2016/6/14 17:02
 */
public class WeatherInfo implements Serializable {
    String date;
    String coldIndex;
    String dressingIndex;
    String humidity;
    String pollutionIndex;
    String sunrise;
    String sunset;
    String wind;
    String exerciseIndex;
    String temperature;
    private long timeStamp; // 保存时候的时间戳

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getColdIndex() {
        return coldIndex;
    }

    public void setColdIndex(String coldIndex) {
        this.coldIndex = coldIndex;
    }

    public String getDressingIndex() {
        return dressingIndex;
    }

    public void setDressingIndex(String dressingIndex) {
        this.dressingIndex = dressingIndex;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPollutionIndex() {
        return pollutionIndex;
    }

    public void setPollutionIndex(String pollutionIndex) {
        this.pollutionIndex = pollutionIndex;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getExerciseIndex() {
        return exerciseIndex;
    }

    public void setExerciseIndex(String exerciseIndex) {
        this.exerciseIndex = exerciseIndex;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
