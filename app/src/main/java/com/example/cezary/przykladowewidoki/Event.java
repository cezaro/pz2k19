package com.example.cezary.przykladowewidoki;

import android.graphics.Color;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Event implements Serializable {
    public Integer id;
    public String name;
    public String place;
    public double placeLatitude;
    public double placeLongitude;
    public LocalDateTime startDate;
    public LocalDateTime endDate;
    public boolean wantNotification;
    private int travelTime;

    public Event() {}

    public Event(Integer id, String name, String place, double placeLatitude, double placeLongitude, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Event(Integer id, String name, String place, double placeLatitude, double placeLongitude, long startDate, long endDate, boolean wantNotification) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.startDate = new LocalDateTime(startDate * 1000L);
        this.endDate = new LocalDateTime(endDate * 1000L);
        this.wantNotification = wantNotification;
    }

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getDateText() {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");

        return "od " + startDate.toString(fmt) + " do " + endDate.toString(fmt);
    }

    public boolean getWantNotification(){
        return wantNotification;
    }

    public void setWantNotification(boolean wantNotification){this.wantNotification = wantNotification;}

    public com.github.sundeepk.compactcalendarview.domain.Event getCalendarEventObject() {

        return new com.github.sundeepk.compactcalendarview.domain.Event(Color.rgb(61, 90, 254), this.startDate.toDateTime().getMillis());
    }

    public int getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(int travelTime) {
        this.travelTime = travelTime;
    }
}
