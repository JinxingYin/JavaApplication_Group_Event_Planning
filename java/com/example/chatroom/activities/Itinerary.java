package com.example.chatroom.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatroom.adapters.Itinerary.Event;
import com.example.chatroom.adapters.Itinerary.EventAdapter;
import com.example.chatroom.R;

import java.util.ArrayList;

public class Itinerary extends AppCompatActivity
{
    Button backBTN;
    private int day, month, year;
    private ListView eventLV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);
        initWidgets();

//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
//                day = i;
//                month = i1 + 1;
//                year = i2;
//            }
//        });
    }

    private void initWidgets()
    {
        backBTN = (Button) findViewById(R.id.backBTN);
        eventLV = (ListView) findViewById(R.id.eventLV);
    }

    public void BackToMainAction(View view) {
        backBTN.setOnClickListener(v-> onBackPressed());
    }

    public void newEventAction(View view) {
        startActivity(new Intent(this, ItineraryEditActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setEventAdapter();
    }

    private void setEventAdapter() {
        ArrayList<Event> dailyEvents = Event.eventsForDate();
        EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), dailyEvents);
        eventLV.setAdapter(eventAdapter);
    }


}


