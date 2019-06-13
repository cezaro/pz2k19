package com.example.cezary.przykladowewidoki;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static java.lang.Integer.parseInt;


public class NotificationIntentService extends IntentService{

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";


    static LocalDateTime eventStart = LocalDateTime.now().plusDays(1);
    public static String name = "Brak";
    Period differce;
    static int  minutes;
    static int durationMinutes;
    static double eventLatitude, eventLongitude, currentLatitude, currentLongitude;
    ArrayList<Event> events = new ArrayList<Event>();
    static String url;
    static Event event;

    public NotificationIntentService() {

        super(NotificationIntentService.class.getSimpleName());
        events = MainActivity.events;
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exc downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        Log.d("Data =", "url"+data.toString());
        return data;
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

    @Override
    protected void onHandleIntent(Intent intent) {
        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){
           currentLatitude = gps.getLatitude(); // returns latitude
           currentLongitude = gps.getLongitude();
        }
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        for (int i = events.size() - 1; i >= 0; i-- )
            if (events.get(i).getStartDate().isAfter(LocalDateTime.now()) && events.get(i).getWantNotification()) {
                name = events.get(i).getName();
                eventStart = events.get(i).getStartDate();
                differce = new Period(LocalDateTime.now(), eventStart, PeriodType.minutes());
                minutes =  differce.getMinutes();
                eventLatitude = events.get(i).placeLatitude;
                eventLongitude = events.get(i).placeLongitude;
                event = events.get(i);
            }
        String str_origin = "origins=" + currentLatitude + "," + currentLongitude;
        // Destination of route
        String str_dest = "destinations=" + eventLatitude + "," + eventLongitude;
        // Sensor enabled
        String units = "units=metric";
        // Travelling mode enable
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters =units + "&" + str_origin + "&" + str_dest;
        // Output format
        String output = "json";

        if(events.size()>0){
            url = "https://maps.googleapis.com/maps/api/distancematrix/" + output + "?" + parameters + "&key=" + loadKey();
            Log.i("............",url);
        }

        String data = "";
        try
        {
            // Fetching the data from web service
            data = NotificationIntentService.this.downloadUrl(url);
        } catch (Exception e)
        {
            Log.d("Background Task", e.toString());
        }

        JSONObject jObject;
        try
        {
            jObject = new JSONObject(data);
            DirectionsJSONParser parser = new DirectionsJSONParser();
            NotificationIntentService.this.durationMinutes = parser.parseInt(jObject);

            Log.i("++++++++++++++++++++++", "Pobrano czas: " + NotificationIntentService.this.durationMinutes);

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)&&eventStart.isBefore(LocalDateTime.now().plusMinutes(10 + durationMinutes))) {
                System.out.println(url);
                processStartNotification(name, minutes, durationMinutes, url);
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void processStartNotification(String name, int minutes, int duration, String url) {
        // Do something. For example, fetch fresh data from backend to create a rich notification?

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            if(minutes - duration >= 0)
                builder.setContentTitle("Przypominienie o wyjsciu! " + name)
                        .setAutoCancel(true)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .setContentText("Musisz wyjsc za " + (minutes - duration) + "'")
                        .setSmallIcon(R.drawable.notification_icon)
                        .setSound(soundUri);
            else
                builder.setContentTitle("Przypominienie o wyjsciu! " + name)
                        .setAutoCancel(true)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .setContentText("Jestes spozniony o  " + (duration - minutes) + "'")
                        .setSmallIcon(R.drawable.notification_icon)
                        .setSound(soundUri);

            Intent mainIntent = new Intent(this, NotificationEvent.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    NOTIFICATION_ID,
                    mainIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

            final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, builder.build());
        }
}

