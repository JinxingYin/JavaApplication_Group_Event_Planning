package com.example.chatroom.utilities;

public class ItineraryUtils {

    public static int day, month, year, hour, minute;

    public static String formattedDate(int day, int month, int year) {

        return month + "/" + day + "/" + year;

    }

    public static String formattedTime(int hour, int minute) {

        return hour + ":" + minute;

    }

    public static String formattedStringTime(String hour, String minute) {

        String formatter = hour + ":" + minute;
        return formatter;

    }


}



