package com.example.cezary.przykladowewidoki;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
    Button addBtn, delBtn;
    DatePickerDialog datePickerDialog;

    final Calendar calendar = Calendar.getInstance();
    TimePickerDialog timePickerDialog;


    String eventPlace;
    int sYear, sMonth, sDay, sHour, sMinute, eYear, eMonth, eDay, eHour, eMinute;

    double eventLatitude, eventLongitude;

    AutocompleteSupportFragment autocompleteFragment;
    EditText autocompleteFragmentInput;

    private Manager dbManager;

    Event event = new Event();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Dodaj wydarzenie");

        String languageToLoad  = "pl";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        dbManager = new Manager(this);
        dbManager.open();

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
        delBtn = (Button)findViewById(R.id.delButton);
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

        if (EventView.selectedEvent != null){
            mNameTXT.setText(EventView.selectedEvent.getName());

            autocompleteFragment.setText(EventView.selectedEvent.getPlace());

            event.place = EventView.selectedEvent.getPlace();
            event.placeLongitude = EventView.selectedEvent.placeLongitude;
            event.placeLatitude = EventView.selectedEvent.placeLatitude;

            fmt = DateTimeFormat.forPattern("HH:mm");
            mStartTimeTXT.setText(EventView.selectedEvent.startDate.toString(fmt));
            mEndTimeTXT.setText(EventView.selectedEvent.endDate.toString(fmt));
            fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
            mStartDateTXT.setText(EventView.selectedEvent.startDate.toString(fmt));
            delBtn.setVisibility(View.VISIBLE);
        }

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

                event.name = mNameTXT.getText().toString();
                event.startDate = new LocalDateTime(sYear, sMonth, sDay, sHour, sMinute);
                event.endDate = new LocalDateTime(sYear, sMonth, sDay, eHour, eMinute);


                if (EventView.selectedEvent != null) {
                    MainActivity.events.remove(EventView.selectedEvent);
                    dbManager.deleteEvent(EventView.selectedEvent.getId());
                }

                if(MainActivity.actualDate.getDayOfYear() == event.startDate.getDayOfYear() && MainActivity.actualDate.getYear() == event.startDate.getYear())
                {
                    MainActivity.events.add(event);
                }

                dbManager.insertEvent(event);

                Intent data = getIntent();
//                data.putExtra("name", mNameTXT.getText().toString());
//                data.putExtra("place", eventPlace);
//                data.putExtra("startyear", sYear);
//                data.putExtra("startmonth", sMonth);
//                data.putExtra("startday", sDay);
//                data.putExtra("starthour", sHour);
//                data.putExtra("startminute", sMinute);
//                data.putExtra("endhour", eHour);
//                data.putExtra("endminute", eMinute);

                setResult(RESULT_OK, data);
                MainActivity.refreshEvents();
                finish();
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);
                builder.setMessage("Czy chcesz usunąć to wydarzenie?")
                        .setPositiveButton("TAK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.events.remove(EventView.selectedEvent);
                                dbManager.deleteEvent(EventView.selectedEvent.getId());
                                MainActivity.refreshEvents();
                                finish();
                            }
                        })
                        .setNegativeButton("NIE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        }).create();
                builder.show();
            }
        });

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                event.place = place.getName();
                event.placeLatitude = place.getLatLng().latitude;
                event.placeLongitude = place.getLatLng().longitude;

                Log.d("LOGI", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                Log.d("LOGI", "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        dbManager.close();
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
