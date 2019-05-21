package com.example.cezary.przykladowewidoki;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NotificationEvent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstnaceState) {
        super.onCreate(savedInstnaceState);
        setContentView(R.layout.activity_notification);

        TextView textView = (TextView) findViewById(R.id.name);
        textView.setText(NotificationIntentService.name);
        TextView textView1 = (TextView) findViewById(R.id.time);
        textView1.setText("    Musisz wyjsc za " + NotificationIntentService.minutes +"'");
        FloatingActionButton closeButton = findViewById(R.id.fab);
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                NotificationEventReceiver.cancelAlarm(getApplication());
                finish();
            }
        });

    }
}
