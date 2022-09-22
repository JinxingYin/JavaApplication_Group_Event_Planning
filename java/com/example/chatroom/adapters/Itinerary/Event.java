package com.example.chatroom.adapters.Itinerary;

import java.util.ArrayList;

public class Event {

    public static ArrayList<Event> eventsList = new ArrayList<>();

    public static ArrayList<Event> eventsForDate(){
        ArrayList<Event> events = new ArrayList<>();
        for(Event event : eventsList){
                        events.add(event);
        }
        return events;
    }

    private String name;
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String getStringDay() {
        return Integer.toString(day);
    }

    public String getStringMonth() {
        return Integer.toString(month);
    }

    public String getStringYear() {
        return Integer.toString(year);
    }

    public Event(String name, int day, int month, int year, int hour, int minute) {
        this.name = name;
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
    }

    public String getDate() {
        return this.month + "/" + this.day + "/" + this.year;
    }

    public void setDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return this.hour + ":" + this.minute;
    }
}


