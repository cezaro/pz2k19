package com.example.cezary.przykladowewidoki;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

    EditText mNameTXT, mPlaceTXT, mStartDateTXT, mStartTimeTXT, mEndDateTXT, mEndTimeTXT;
    Button addBtn;
    DatePickerDialog datePickerDialog;
    final Calendar calendar = Calendar.getInstance();
    TimePickerDialog timePickerDialog;
    int sYear, sMonth, sDay, sHour, sMinute,
            eYear, eMonth, eDay, eHour, eMinute;

    int rnd(int minute){
        float min = minute/5.0f;
        Math.round(min);
        minute = (int) min * 5;
        return minute;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Dodaj wydarzenie");
        setContentView(R.layout.activity_create_event);

        mNameTXT = (EditText)findViewById(R.id.nameTXT);
        mPlaceTXT = (EditText)findViewById(R.id.placeTXT);
        mStartDateTXT = (EditText)findViewById(R.id.startDateTXT);
        mEndDateTXT = (EditText)findViewById(R.id.endDateTXT);
        mStartTimeTXT = (EditText)findViewById(R.id.startTimeTXT);
        mEndTimeTXT = (EditText)findViewById(R.id.endTimeTXT);
        addBtn = (Button)findViewById(R.id.addButton);
        mStartDateTXT.setKeyListener(null);
        mEndDateTXT.setKeyListener(null);
        mStartTimeTXT.setKeyListener(null);
        mEndTimeTXT.setKeyListener(null);

        mStartTimeTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new CustomTimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        sHour = hourOfDay;
                        sMinute = rnd(minute);
                        mStartTimeTXT.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
        mEndTimeTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new CustomTimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        eHour = hourOfDay;

                        eMinute = rnd(minute);
                        mEndTimeTXT.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();

            }
        });


        mStartDateTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        sYear = year;
                        sMonth = month + 1;
                        sDay = dayOfMonth;
                        mStartDateTXT.setText(dayOfMonth + "/" + month + "/" + year);
                        eYear = year;
                        eMonth = month;
                        eDay = dayOfMonth;
                        mEndDateTXT.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        mEndDateTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder errorAlert = new AlertDialog.Builder(CreateEventActivity.this);
                errorAlert.setMessage("Wydarzenie musi zaczynac sie i konczyc tego samego dnia.")
                        .create();
                errorAlert.show();
            }
        });
    /*    mEndDateTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        eYear = year;
                        eMonth = month;
                        eDay = dayOfMonth;
                        mEndDateTXT.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });*/


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (eHour < sHour || (eHour == sHour && eMinute < sMinute + 15)) {

                    AlertDialog.Builder errorAlert = new AlertDialog.Builder(CreateEventActivity.this);
                    errorAlert.setMessage("Blad: Nie mozna stowrzyc wydarzenia, ktore trwa mniej niz 15 minut.")
                            .create();
                    errorAlert.show();
                    return;
                }

                Intent data = getIntent();
                data.putExtra("name", mNameTXT.getText().toString());
                data.putExtra("place", mPlaceTXT.getText().toString());
                data.putExtra("startyear", sYear);
                data.putExtra("startmonth", sMonth);
                data.putExtra("startday", sDay);
                data.putExtra("starthour", sHour);
                data.putExtra("startminute", sMinute);
                data.putExtra("endyear", eYear);
                data.putExtra("endmonth", eMonth);
                data.putExtra("endday", eDay);
                data.putExtra("endhour", eHour);
                data.putExtra("endminute", eMinute);

                setResult(RESULT_OK, data);
                finish();
            }
        });
    }
}
