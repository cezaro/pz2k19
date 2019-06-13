package com.example.cezary.przykladowewidoki;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TravelTimeView extends LinearLayout {
    TextView travelTimeText;
    Integer time = 0;

    public TravelTimeView(Context context, AttributeSet attrs, Integer time) {
        super(context, attrs);
        init(context, attrs);
        this.time = time;

        travelTimeText.setText("Czas podróży " + this.time + " min");
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.travel_time_view, this);

        travelTimeText = findViewById(R.id.travelTimeText);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        return true;
    }
}
