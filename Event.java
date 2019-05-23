package com.example.cezary.przykladowewidoki;

import android.graphics.Color;
import android.support.annotation.NonNull;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Event implements Serializable {
    public Integer id;
    public String name, place;
    public LocalDateTime startDate;
    public LocalDateTime endDate;

    public Event(Integer id,String name, String place, LocalDateTime startDate, LocalDateTime endDate) {
        this.id =id;
        this.name = name;
        this.place = place;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public com.github.sundeepk.compactcalendarview.domain.Event getCalendarEventObject() {

        return new com.github.sundeepk.compactcalendarview.domain.Event(Color.rgb(61, 90, 254), this.startDate.toDateTime().getMillis());
    }
}