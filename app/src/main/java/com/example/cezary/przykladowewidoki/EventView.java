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

public class EventView extends LinearLayout {
    Event event;

    TextView nameText,
            placeText,
            dateText;

    public EventView(Context context, AttributeSet attrs, Event event) {
        super(context, attrs);
        init(context, attrs);
        this.event = event;

        nameText.setText(event.name);
        placeText.setText(event.place);
        dateText.setText(event.getDateText());
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.event_view, this);

        nameText = findViewById(R.id.eventName);
        placeText = findViewById(R.id.eventPlace);
        dateText = findViewById(R.id.eventDate);

        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), "asd", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        return true;
    }

    private void initComponents() {

       /* artistText = (TextView) findViewById(R.id.artist_Text);

        trackText = (TextView) findViewById(R.id.track_Text);

        buyButton = (Button) findViewById(R.id.buy_Button);*/
    }



    public void expandEvent(View view) {
        Log.d("test","dziala");

//        CardView card = view.findViewById(R.id.firstCard);

//        LinearLayout buttons = card.findViewById(R.id.buttons);


//        buttons.setVisibility(View.VISIBLE);

    }
}
