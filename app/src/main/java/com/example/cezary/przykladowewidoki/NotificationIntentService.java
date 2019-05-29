package com.example.cezary.przykladowewidoki;

import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;


public class NotificationIntentService extends IntentService{

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";


    static LocalDateTime event = LocalDateTime.now().plusDays(1);
    public static String name = "Brak";
    Period differce;
    static int  minutes;
    static double eventLatitude, eventLongitude, currentLatitude, currentLongitude;


    public NotificationIntentService() {

        super(NotificationIntentService.class.getSimpleName());
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
        for (int i = MainActivity.events.size() - 1; i >= 0; i-- )
            if (MainActivity.events.get(i).getStartDate().isAfter(LocalDateTime.now())) {
                name = MainActivity.events.get(i).getName();
                event = MainActivity.events.get(i).getStartDate();
                differce = new Period(LocalDateTime.now(), event, PeriodType.minutes());
                minutes =  differce.getMinutes();
                eventLatitude = MainActivity.events.get(i).placeLatitude;
                eventLongitude = MainActivity.events.get(i).placeLongitude;
            }
        String str_origin = "origin=" + currentLatitude + "," + currentLongitude;
        // Destination of route
        String str_dest = "destination=" + eventLatitude + "," + eventLongitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Travelling mode enable
        String mode = "mode = driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"+ mode;
        // Output format
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + loadKey();
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)&&event.isBefore(LocalDateTime.now().plusMinutes(10))) {

                processStartNotification(name, minutes);
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

    private void processStartNotification(String name, int minutes) {
        // Do something. For example, fetch fresh data from backend to create a rich notification?


            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("Przypominienie o wyjsciu! " + name)
                    .setAutoCancel(true)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setContentText("Musisz wyjsc za " + minutes)
                    .setSmallIcon(R.drawable.notification_icon);

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