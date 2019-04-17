package com.example.cezary.przykladowewidoki;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<Event> events = new ArrayList<Event>();

    LinearLayout eventsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("d MMMM");

        toolbar.setTitle(dateformat.format(calendar.getTime()));
        setSupportActionBar(toolbar);

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
        // Handle navigation view item clicks here.
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
        /*Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);*/

        createEvent(new Event("Nowe wydarzenie", "Nowe miejsce", Calendar.getInstance(), Calendar.getInstance()));
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
}
