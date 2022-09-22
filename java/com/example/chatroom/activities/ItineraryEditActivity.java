package com.example.chatroom.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatroom.adapters.Itinerary.Event;
import com.example.chatroom.R;

import java.util.Calendar;
import java.util.Locale;

public class ItineraryEditActivity extends AppCompatActivity {

    private EditText eventNameET;
    private Button timeBTN, dateBTN;
    private Event newEvent;
    private int day, month, year, hr, min;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_edit);
        initWidgets();
        initDatePicker();
    }

    private void initWidgets() {
        eventNameET = findViewById(R.id.eventName);
        timeBTN = findViewById(R.id.timeBTN);
        dateBTN = findViewById(R.id.dateBTN);
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    public void saveEventAction(View view) {
        String eventName = eventNameET.getText().toString();
        newEvent = new Event(eventName, day, month, year, hr, min);
        Event.eventsList.add(newEvent);
        finish();
    }

    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHR, int selectedMIN) {
                hr = selectedHR;
                min = selectedMIN;
                timeBTN.setText(String.format(Locale.getDefault(), "%02d:%02d", hr, min));
            }
        };

        int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog =  new TimePickerDialog(this, onTimeSetListener, hr, min, false);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                m = m + 1;
                day = d;
                month = m;
                year = y;
                String date = makeDateString(d,m,y);
                dateBTN.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, y, m, d);
    }

    private String makeDateString(int d, int m, int y) {
        return getMonthFormat(m) + " " + d + " " + y;
    }

    private String getMonthFormat(int m) {
        if(m == 1)
            return "JAN";
        if(m == 2)
            return "FEB";
        if(m == 3)
            return "MAR";
        if(m == 4)
            return "APR";
        if(m == 5)
            return "MAY";
        if(m == 6)
            return "JUN";
        if(m == 7)
            return "JUL";
        if(m == 8)
            return "AUG";
        if(m == 9)
            return "SEP";
        if(m == 10)
            return "OCT";
        if(m == 11)
            return "NOV";
        if(m == 12)
            return "DEC";

        //default
        return "NAN";

    }

    public void datePicker(View view) {
        datePickerDialog.show();
    }
}

