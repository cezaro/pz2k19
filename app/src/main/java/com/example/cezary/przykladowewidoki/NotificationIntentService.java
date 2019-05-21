package com.example.cezary.przykladowewidoki;

import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.cezary.przykladowewidoki.R;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.time.temporal.ChronoUnit;


public class NotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    static LocalDateTime event = LocalDateTime.now().plusDays(1);
    public static String name = "Brak";
    Period differce;
    static int  minutes;

    //= MainActivity.events.get(0).getStartDate();

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

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        for (int i = MainActivity.events.size() - 1; i > 0; i-- )
            if (MainActivity.events.get(i).getStartDate().isAfter(LocalDateTime.now())) {
                name = MainActivity.events.get(i).getName();
                event = MainActivity.events.get(i).getStartDate();
                differce = new Period(LocalDateTime.now(), event, PeriodType.minutes());
                minutes =  differce.getMinutes();
            }
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