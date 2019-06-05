package com.example.cezary.przykladowewidoki;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;

public class Manager {
    private static final String DEBUG_TAG = "SqLiteTodoManager";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "base.db";
    private static final String DB_EVENTS_TABLE = "events";

    public static final String KEY_ID = "_id";
    public static final int ID_COLUMN = 0;

    public static final String KEY_NAME = "name";
    public static final int NAME_COLUMN = 1;

    public static final String KEY_PLACE = "place";
    public static final int PLACE_COLUMN = 2;

    public static final String KEY_PLACE_LATITUDE = "place_latitude";
    public static final int PLACE_LATITUDE_COLUMN = 3;

    public static final String KEY_PLACE_LONGITUDE = "place_longitude";
    public static final int PLACE_LONGITUDE_COLUMN = 4;

    public static final String KEY_START_DATE = "start_date";
    public static final int START_DATE_COLUMN = 5;

    public static final String KEY_END_DATE = "end_date";
    public static final int END_DATE_COLUMN = 6;

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    private static final String DB_CREATE_EVENTS_TABLE = "CREATE TABLE `events` (" +
        "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
        "`name` TEXT," +
        "`place` TEXT," +
        "`place_latitude` NUMERIC," +
        "`place_longitude` NUMERIC," +
        "`start_date` INTEGER," +
        "`end_date` INTEGER" +
    ")";

    private static final String DROP_EVENTS_TABLE = "DROP TABLE IF EXISTS events " ;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_EVENTS_TABLE);

            Log.d(DEBUG_TAG, "Database creating...");
            Log.d(DEBUG_TAG, "Table " + DB_CREATE_EVENTS_TABLE + " ver." + DB_VERSION + " created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_EVENTS_TABLE);

            Log.d(DEBUG_TAG, "Database updating...");
            Log.d(DEBUG_TAG, "Table " + DB_EVENTS_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "All data is lost.");

            onCreate(db);
        }
    }

    public Manager(Context context) {
        this.context = context;
    }

    public Manager open(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public int insertEvent(Event event) {
        ContentValues newEventValues = new ContentValues();
        newEventValues.put(KEY_NAME, event.name);
        newEventValues.put(KEY_PLACE, event.place);
        newEventValues.put(KEY_PLACE_LATITUDE, event.placeLatitude);
        newEventValues.put(KEY_PLACE_LONGITUDE, event.placeLongitude);
        newEventValues.put(KEY_START_DATE, event.startDate.toDateTime().getMillis()/1000);
        newEventValues.put(KEY_END_DATE, event.endDate.toDateTime().getMillis()/1000);

        event.id = (int) db.insert(DB_EVENTS_TABLE, null, newEventValues);
        return event.id;
    }


    public boolean updateEvent(Event event) {
        int id = event.getId();
        String name = event.getName();
        long  startDate = event.startDate.toDateTime().getMillis()/1000;
        long  endDate = event.endDate.toDateTime().getMillis()/1000;
        String place = event.getPlace();

        return updateEvent(id, name, startDate, endDate,place);
    }

    public boolean updateEvent(Integer id, String name, long startDate, long endDate, String place ) {
        String where = KEY_ID + "=" + id;

        ContentValues updateEventValues = new ContentValues();

        updateEventValues.put(KEY_NAME, name);
        updateEventValues.put(KEY_START_DATE, startDate);
        updateEventValues.put(KEY_END_DATE, endDate);
        updateEventValues.put(KEY_PLACE, place);
        return db.update(DB_EVENTS_TABLE, updateEventValues, where, null) > 0;
    }

    public boolean deleteEvent(long id){
        String where = KEY_ID + "=" + id;

        return db.delete(DB_EVENTS_TABLE, where, null) > 0;
    }

    public Cursor getAllEvent() {

        String[] columns = {KEY_ID, KEY_NAME, KEY_START_DATE, KEY_END_DATE, KEY_PLACE};
        return db.query(DB_EVENTS_TABLE, columns, null, null, null, null, null);
    }

    public ArrayList<Event> getDayEvents(LocalDateTime date) {
        long dayStart = date.toLocalDate().toDateTimeAtStartOfDay().getMillis() / 1000L;
        long endStart = date.plusDays(1).toLocalDate().toDateTimeAtStartOfDay().getMillis() / 1000L;

        Cursor cursor = db.rawQuery("SELECT * FROM `" + DB_EVENTS_TABLE + "` WHERE `start_date` >= '" + dayStart + "' AND `start_date` <= '" + endStart + "'", null);
        return parseEvents(cursor);
    }

    public ArrayList<Event> getMonthEvents(LocalDateTime date) {
        long monthStart = date.dayOfMonth().withMinimumValue().toDateTime().withTimeAtStartOfDay().getMillis() / 1000L;
        long monthEnd = date.plusMonths(1).dayOfMonth().withMinimumValue().toDateTime().withTimeAtStartOfDay().getMillis() / 1000L;

        Cursor cursor = db.rawQuery("SELECT * FROM `" + DB_EVENTS_TABLE + "` WHERE `start_date` >= '" + monthStart + "' AND `start_date` <= '" + monthEnd + "'", null);
        return parseEvents(cursor);
    }

    public Event getEvent(Integer id) {
        String[] columns = {KEY_ID, KEY_NAME, KEY_PLACE, KEY_PLACE_LATITUDE, KEY_PLACE_LONGITUDE, KEY_START_DATE, KEY_END_DATE};
        String where = KEY_ID + "=" + id;
        Cursor cursor = db.query(DB_EVENTS_TABLE, null, where, null, null, null, null);
        Event event = null;

        if(cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(NAME_COLUMN);
            String place = cursor.getString(PLACE_COLUMN);
            double placeLat = cursor.getDouble(PLACE_LATITUDE_COLUMN);
            double placeLng = cursor.getDouble(PLACE_LONGITUDE_COLUMN);
            long startDate = cursor.getLong(START_DATE_COLUMN);
            long endDate = cursor.getLong(END_DATE_COLUMN);

            event = new Event(id, name, place, placeLat, placeLng, startDate, endDate, true);
        }

        return event;
    }

    private ArrayList<Event> parseEvents(Cursor cursor) {
        ArrayList<Event> events = new ArrayList<>();

        if(cursor != null) {
            while(cursor.moveToNext()) {
                int id = cursor.getInt(ID_COLUMN);
                String name = cursor.getString(NAME_COLUMN);
                String place = cursor.getString(PLACE_COLUMN);
                double placeLat = cursor.getDouble(PLACE_LATITUDE_COLUMN);
                double placeLng = cursor.getDouble(PLACE_LONGITUDE_COLUMN);
                long startDate = cursor.getLong(START_DATE_COLUMN);
                long endDate = cursor.getLong(END_DATE_COLUMN);

                events.add(new Event(id, name, place, placeLat, placeLng, startDate, endDate, true));
            }
        }

        return events;
    }
}
