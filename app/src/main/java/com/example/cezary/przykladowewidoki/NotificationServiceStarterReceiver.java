package com.example.cezary.przykladowewidoki;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

public final class NotificationServiceStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationEventReceiver.setupAlarm(context);
    }
}