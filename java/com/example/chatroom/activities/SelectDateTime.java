package com.example.chatroom.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;
import com.example.chatroom.databinding.ActivitySelectDateTimeBinding;
import com.example.chatroom.utilities.Constants;
import com.example.chatroom.utilities.PreferenceManager;
import java.util.Locale;

//an activity to setup an event
public class SelectDateTime extends AppCompatActivity {
    private ActivitySelectDateTimeBinding binding;
    private int hour,minute;
    private PreferenceManager preferenceManager;
    private String EventName,EventDescription, EventDate, EventTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectDateTimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListener();
    }

    // button click listeners
    private void setListener(){
        //select time for the event
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        binding.timeButton.setOnClickListener(view -> {
            TimePickerDialog.OnTimeSetListener  onTimeSetListener
                    = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int Hour, int Minute) {
                            hour = Hour;
                            minute = Minute;
                            EventTime = (String.format(Locale.getDefault(),"%02d:%02d",hour,minute));
                            binding.timeButton.setText(String.format(Locale.getDefault(),"%02d:%02d",hour,minute));
                            preferenceManager.putString(Constants.KEY_EVENT_TIME,EventTime);
                }
            };
            TimePickerDialog timePickerDialog =new TimePickerDialog(this,onTimeSetListener,hour,minute,true);
            timePickerDialog.show();
        });
        //select date for the event
        binding.dateButton.setOnClickListener(view->{
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month = month+1;
                    String date = month + "/ " + day+ "/ "+ year;
                    EventDate = date;
                    binding.dateButton.setText(date);
                    preferenceManager.putString(Constants.KEY_EVENT_DATE,EventDate);
                }
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener,2022,5,5);
            datePickerDialog.show();
        });
        //go to the next page and keep the event name and event description
        binding.nextButton.setOnClickListener(view->{
            EventName = binding.inputEventName.getText().toString();
            preferenceManager.putString(Constants.KEY_EVENT,EventName);
            EventDescription = binding.inputEventDescription.getText().toString();
            preferenceManager.putString(Constants.KEY_EVENT_DESCRIPTION,EventDescription);
            startActivity(new Intent(getApplicationContext(),SelectUsersToEvent.class));
        });
    }
}