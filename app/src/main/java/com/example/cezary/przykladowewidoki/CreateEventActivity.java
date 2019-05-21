package com.example.cezary.przykladowewidoki;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;

public class CreateEventActivity extends AppCompatActivity {
    EditText mNameTXT, mPlaceTXT, mStartDateTXT, mStartTimeTXT, mEndTimeTXT;
    Button addBtn;
    DatePickerDialog datePickerDialog;

    final Calendar calendar = Calendar.getInstance();
    TimePickerDialog timePickerDialog;


    String eventPlace;
    int sYear, sMonth, sDay, sHour, sMinute, eYear, eMonth, eDay, eHour, eMinute;

    AutocompleteSupportFragment autocompleteFragment;
    EditText autocompleteFragmentInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Dodaj wydarzenie");

        Configuration config = getBaseContext().getResources().getConfiguration();
        Locale locale = new Locale("pl");
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());


        setContentView(R.layout.activity_create_event);

        Places.initialize(getApplicationContext(), loadKey());
        PlacesClient placesClient = Places.createClient(this);

        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        ((View)findViewById(R.id.places_autocomplete_search_button)).setVisibility(View.GONE);

        autocompleteFragment.setHint("Miejsce");

        autocompleteFragmentInput = (EditText)findViewById(R.id.places_autocomplete_search_input);
        autocompleteFragmentInput.setTextSize(18.0f);
        autocompleteFragmentInput.setBackground(getResources().getDrawable(R.drawable.edit_text_background));
        autocompleteFragmentInput.setPadding(15, 15, 15, 15);


        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        mNameTXT = (EditText)findViewById(R.id.nameTXT);
        mStartDateTXT = (EditText)findViewById(R.id.startDateTXT);
        mStartTimeTXT = (EditText)findViewById(R.id.startTimeTXT);
        mEndTimeTXT = (EditText)findViewById(R.id.endTimeTXT);
        addBtn = (Button)findViewById(R.id.addButton);
        mStartDateTXT.setKeyListener(null);
        mStartTimeTXT.setKeyListener(null);
        mEndTimeTXT.setKeyListener(null);

        // Filling inputs with actual time and date
        LocalDateTime now = LocalDateTime.now().secondOfMinute().roundFloorCopy();

        if(now.getMinuteOfHour() % 5 != 0)
        {
            int toRounded = now.getMinuteOfHour() % 5;
            now = now.plusMinutes(5 - toRounded);
        }

        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
        mStartTimeTXT.setText(now.toString(fmt));
        sMinute = now.getMinuteOfHour();
        sHour = now.getHourOfDay();

        now = now.plusMinutes(15);
        mEndTimeTXT.setText(now.toString(fmt));
        eMinute = now.getMinuteOfHour();
        eHour = now.getHourOfDay();

        fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
        mStartDateTXT.setText(now.toString(fmt));

        sDay = now.getDayOfMonth();
        sMonth = now.getMonthOfYear();
        sYear = now.getYear();

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
                    }
                }, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eHour < sHour || (eHour == sHour && eMinute < sMinute + 15))
                {
                    AlertDialog.Builder errorAlert = new AlertDialog.Builder(CreateEventActivity.this);
                    errorAlert.setMessage("Blad: Nie mozna stowrzyc wydarzenia, ktore trwa mniej niz 15 minut.")
                            .create();
                    errorAlert.show();
                    return;
                }

                Intent data = getIntent();
                data.putExtra("name", mNameTXT.getText().toString());
                data.putExtra("place", eventPlace);
                data.putExtra("startyear", sYear);
                data.putExtra("startmonth", sMonth);
                data.putExtra("startday", sDay);
                data.putExtra("starthour", sHour);
                data.putExtra("startminute", sMinute);
                data.putExtra("endhour", eHour);
                data.putExtra("endminute", eMinute);

                setResult(RESULT_OK, data);
                finish();
            }
        });

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                eventPlace = place.getName();

                Log.i("MAPSAPI", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("MAPSAPI", "An error occurred: " + status);
            }
        });
    }

    private String loadKey() {
        AssetManager assetManager = getResources().getAssets();

        try {
            InputStream inputStream = assetManager.open("keystore.properties");
            Properties properties = new Properties();
            properties.load(inputStream);

            return properties.getProperty("googleMapsApiKey");
        } catch (IOException e) {
            System.err.println("Nie znaleziono pliku");
            e.printStackTrace();
        }

        return null;
    }

    int rnd(int minute){
        float min = minute/5.0f;
        Math.round(min);
        minute = (int) min * 5;
        return minute;
    }
}
