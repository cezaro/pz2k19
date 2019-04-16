package com.example.cezary.przykladowewidoki;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Event implements Serializable {

    public String name, place;
    public Calendar startDate;
    public Calendar endDate;

    public Event(String name, String place, Calendar startDate, Calendar endDate) {
        this.name = name;
        this.place = place;
        this.startDate = startDate;
        this.endDate = endDate;
    }

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

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public String getDateText() {
        SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm");

        return "od " + dateformat.format(startDate.getTime()) + " do " + dateformat.format(endDate.getTime());
    }
}
