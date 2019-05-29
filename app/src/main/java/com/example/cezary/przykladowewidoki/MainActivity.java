package com.example.cezary.przykladowewidoki;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static ArrayList<Event> events = new ArrayList<Event>();
    public static Context mContext;
    LinearLayout eventsListView;

    public LocalDateTime actualDate;
    public LocalDateTime tempDate;

    private CompactCalendarView compactCalendarView;
    Toolbar toolbar;

    private Manager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        JodaTimeAndroid.init(this);


        String languageToLoad  = "pl";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        DateTimeZone.setDefault(DateTimeZone.forID("Europe/Warsaw"));

        actualDate = LocalDateTime.now();
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarPopup(v);
            }
        });

        setToolbarText();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        eventsListView = (LinearLayout) findViewById(R.id.eventsList);

        /* DEFAULT DATA */

        dbManager = new Manager(this);
        dbManager.open();

        LocalDateTime start = LocalDateTime.now(),
                end = LocalDateTime.now().plusHours(1);

        events = dbManager.getDayEvents(LocalDateTime.now());
        Log.d("LOGI", events.size() + " ");

//        long test = dbManager.insertEvent(new Event(null, "Spotkanie w Pasażu", "plac Grunwaldzki 22, 50-363 Wrocław", start, end));
        //createEvent();

//

        /*start = start.plusHours(1);
        end = end.plusHours(1);

        createEvent(new Event(null, "Spotkanie na PWr", "C4 Politechnika Wrocławska, Janiszewskiego, Wrocław", start, end));

        start = start.plusHours(1);
        end = end.plusHours(1);

        createEvent(new Event("Spotkanie w Nokii", "Strzegomska 36, 53-611 Wrocław", start, end));

        start = start.minusHours(5);
        end = end.minusHours(5);

        createEvent(new Event("Spotkanie w Comarchu", "Jana Długosza 2-6, 51-162 Wrocław", start, end));

        start = start.minusHours(6);
        end = end.minusHours(6);

        createEvent(new Event("Spotkanie biznesowe w Sky Tower", "Powstańców Śląskich 95, 53-332 Wrocław", start, end));*/
        
        refreshEvents();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showCreateEventView(View view) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivityForResult(intent, 12);
    }

    public static void showEditEventView(View view){
        Intent intent = new Intent(mContext, CreateEventActivity.class);
        mContext.startActivity(intent);
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 12) {
            if (resultCode == RESULT_OK) {
                LocalDateTime start = new LocalDateTime(data.getIntExtra("startyear", 2000), data.getIntExtra("startmonth", 1), data.getIntExtra("startday", 1),
                        data.getIntExtra("starthour", 0), data.getIntExtra("startminute", 0));

                LocalDateTime end = new LocalDateTime(data.getIntExtra("startyear", 2000), data.getIntExtra("startmonth", 1), data.getIntExtra("startday", 1),
                        data.getIntExtra("endhour", 0), data.getIntExtra("endminute", 0));

//                Event event = new Event(null, data.getStringExtra("name"), data.getStringExtra("place"), start, end);
//                createEvent(event);
            }
        }
    }

    public void createEvent(Event event) {
        events.add(event);

        refreshEvents();
    }

    private void refreshEvents() {
        sortEvents();
        eventsListView.removeAllViews();

        int count = 0;
        for(Event e : events)
        {
            EventView v = new EventView(getBaseContext(), null, e);
            eventsListView.addView(v);
            count++;
            /*if(e.startDate.compareTo(LocalDateTime.now()) > 0) {
                EventView v = new EventView(getBaseContext(), null, e);
                eventsListView.addView(v);
                count++;
            }*/
        }

        if(count == 0)
        {
            TextView noEventsText = new TextView(getBaseContext());
            noEventsText.setText("Na dzisiaj nie ma zaplanowanych wydarzeń");
            noEventsText.setGravity(Gravity.CENTER);
            noEventsText.setPadding(0, 15, 0, 15);

            eventsListView.addView(noEventsText);
        }
    }

    private void setToolbarText() {
        LocalDateTime date = actualDate;

        if(isToday(date)) {
            toolbar.setTitle("Dzisiaj");
        } else if(isTomorrow(date)){
            toolbar.setTitle("Jutro");
        } else if(isYesterday(date)){
            toolbar.setTitle("Wczoraj");
        } else {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("d MMMM YYYY");

            toolbar.setTitle(date.toString(fmt));
        }

        setSupportActionBar(toolbar);
    }

    private void showCalendarPopup(View v) {
        final Dialog myDialog = new Dialog(this);
        final String [] months = {"Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"};

        myDialog.setContentView(R.layout.app_compact_calendar_bar);

        compactCalendarView = myDialog.findViewById(R.id.compactcalendarView);
        compactCalendarView.displayOtherMonthDays(false);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setIsRtl(false);
        compactCalendarView.invalidate();

        ArrayList<Event> monthEvents = dbManager.getMonthEvents(LocalDateTime.now());
        for(Event e : monthEvents) {
            compactCalendarView.addEvent(e.getCalendarEventObject());
        }

        Button showPreviousMonthBut = myDialog.findViewById(R.id.prev_button);
        Button showNextMonthBut = myDialog.findViewById(R.id.next_button);
        Button btn = myDialog.findViewById(R.id.selectDayBtn);

        final TextView monthName = myDialog.findViewById(R.id.monthName);

        monthName.setText(months[LocalDateTime.now().getMonthOfYear() - 1] + " " + LocalDateTime.now().getYear());

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        myDialog.show();

        showPreviousMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.scrollLeft();
                compactCalendarView.removeAllEvents();

                LocalDateTime selectedMonth = new LocalDateTime(compactCalendarView.getFirstDayOfCurrentMonth());
                ArrayList<Event> monthEvents = dbManager.getMonthEvents(selectedMonth);
                for(Event e : monthEvents) {
                    compactCalendarView.addEvent(e.getCalendarEventObject());
                }
            }
        });

        showNextMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.scrollRight();
                compactCalendarView.removeAllEvents();

                LocalDateTime selectedMonth = new LocalDateTime(compactCalendarView.getFirstDayOfCurrentMonth());
                ArrayList<Event> monthEvents = dbManager.getMonthEvents(selectedMonth);
                for(Event e : monthEvents) {
                    compactCalendarView.addEvent(e.getCalendarEventObject());
                }
            }
        });

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date dateClicked) {
                tempDate = LocalDateTime.fromDateFields(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                LocalDateTime date = LocalDateTime.fromDateFields(firstDayOfNewMonth);
                int month = date.getMonthOfYear();


                monthName.setText(months[month - 1] + " " + date.getYear());
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualDate = tempDate;
                setToolbarText();
                myDialog.hide();
            }
        });
    }

    private boolean isToday(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();

        if(now.year().equals(date.year()) && now.monthOfYear().equals(date.monthOfYear())) {
            if(now.dayOfMonth().equals(date.dayOfMonth())) {
                return true;
            }
        }

        return false;
    }

    private boolean isTomorrow(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();
        now = now.plusDays(1);

        if(now.year().equals(date.year()) && now.monthOfYear().equals(date.monthOfYear())) {
            if(now.dayOfMonth().equals(date.dayOfMonth())) {
                return true;
            }
        }

        return false;
    }

    private boolean isYesterday(LocalDateTime date) {
        LocalDateTime now = new LocalDateTime();
            now = now.minusDays(1);

        if(now.year().equals(date.year()) && now.monthOfYear().equals(date.monthOfYear())) {
            if(now.dayOfMonth().equals(date.dayOfMonth())) {
                return true;
            }
        }

        return false;
    }

    private void sortEvents() {
        Collections.sort(events, new Comparator<Event>() {
            public int compare(Event e1, Event e2) {
                return e1.startDate.compareTo(e2.startDate);
            }
        });
    }

    /*private String loadProperty(String propertyName) {
        AssetManager assetManager = getResources().getAssets();

        try {
            InputStream inputStream = assetManager.open("keystore.properties");
            Properties properties = new Properties();
            properties.load(inputStream);

            return properties.getProperty(propertyName);
        } catch (IOException e) {
            System.err.println("Nie znaleziono pliku");
            e.printStackTrace();
        }

        return null;
    }*/
}
