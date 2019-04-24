package com.example.cezary.przykladowewidoki;

import android.app.Dialog;
import android.content.Intent;
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

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<Event> events = new ArrayList<Event>();

    LinearLayout eventsListView;

    public LocalDateTime actualDate = LocalDateTime.now();
    public LocalDateTime tempDate;


    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);

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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        eventsListView = (LinearLayout) findViewById(R.id.eventsList);

        /* DEFAULT DATA */
        Calendar start = Calendar.getInstance(),
                end = Calendar.getInstance();

        start.set(2019, 4, 18, 8, 0);
        end.set(2019, 4, 18, 9, 0);

        events.add(new Event("Spotkanie w Pasażu", "plac Grunwaldzki 22, 50-363 Wrocław", start, end));

        start.add(Calendar.HOUR, 1);
        end.add(Calendar.HOUR, 1);

        events.add(new Event("Spotkanie na PWr", "C4 Politechnika Wrocławska, Janiszewskiego, Wrocław", start, end));

        start.add(Calendar.HOUR, 1);
        end.add(Calendar.HOUR, 1);

        events.add(new Event("Spotkanie w Nokii", "Strzegomska 36, 53-611 Wrocław", start, end));

        start.add(Calendar.HOUR, 1);
        end.add(Calendar.HOUR, 1);

        events.add(new Event("Spotkanie w Comarchu", "Jana Długosza 2-6, 51-162 Wrocław", start, end));

        start.add(Calendar.HOUR, 1);
        end.add(Calendar.HOUR, 1);

        events.add(new Event("Spotkanie biznesowe w Sky Tower", "Powstańców Śląskich 95, 53-332 Wrocław", start, end));

        
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
            Intent intent = new Intent(this, CalendarActivity.class);
            startActivity(intent);

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
//        createEvent(new Event("Nowe wydarzenie", "Nowe miejsce", Calendar.getInstance(), Calendar.getInstance()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 12){
            if (resultCode == RESULT_OK){
                Calendar start = Calendar.getInstance(),
                    end = Calendar.getInstance();
                start.set(data.getIntExtra("startyear", 2000), data.getIntExtra("startmonth", 1), data.getIntExtra("startday", 1),
                        data.getIntExtra("starthour", 0), data.getIntExtra("startminute", 0));
                end.set(data.getIntExtra("endyear", 2000), data.getIntExtra("endmonth", 1), data.getIntExtra("endday", 1),
                        data.getIntExtra("endhour", 0), data.getIntExtra("endminute", 0));
                Event event = new Event(data.getStringExtra("name"), data.getStringExtra("place"), start, end);
                createEvent(event);
            }
        }
    }
    public void createEvent(Event event) {
        events.add(event);
        eventsListView.addView(new EventView(getBaseContext(), null, event));
    }

    private void refreshEvents() {
        eventsListView.removeAllViews();

        for(Event e : events)
        {
            EventView v = new EventView(getBaseContext(), null, e);
            eventsListView.addView(v);
        }
    }

    private void setToolbarText() {
        LocalDateTime date = actualDate;

        if(isToday(date)) {
            toolbar.setTitle("Dzisiaj");
        } else if(isTomorrow(date)){
            toolbar.setTitle("Jutro");
        } else if(false){
            toolbar.setTitle("Wczoraj");
        } else {
            toolbar.setTitle(date.toString());
        }

        setSupportActionBar(toolbar);
    }

    private void showCalendarPopup(View v) {
        Dialog myDialog = new Dialog(this);

        myDialog.setContentView(R.layout.app_calendar_bar);

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

        Button btn = myDialog.findViewById(R.id.selectDayBtn);
        final CalendarView calendar = myDialog.findViewById(R.id.calendarView);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                tempDate = new LocalDateTime(year, month + 1, dayOfMonth, 12, 0);
                System.out.println(tempDate);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualDate = tempDate;
                setToolbarText();
            }
        });
    }

    private boolean isToday(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();

        if(now.year().equals(date.year())) {
            if(now.monthOfYear().equals(date.monthOfYear())) {
                if(now.dayOfMonth().equals(date.dayOfMonth())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isTomorrow(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();
        now = now.plusDays(1);

        System.out.println(now);
        System.out.println(date);

        if(now.year().equals(date.year())) {
            if(now.monthOfYear().equals(date.monthOfYear())) {
                if(now.dayOfMonth().equals(date.dayOfMonth())) {
                    return true;
                }
            }
        }

        return false;
    }
}
