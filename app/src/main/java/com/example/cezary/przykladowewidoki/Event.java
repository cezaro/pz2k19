package com.example.cezary.przykladowewidoki;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {

    public String name, place;
    public Date date;

    public Event(String name, String place, Date date) {
        this.date = date;
        this.place = place;
        this.name = name;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
