package com.example.scheduler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String day_string[] = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    HashMap<String,Integer> day_to_ind = new HashMap<String,Integer>();

    //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
    Date d = new Date();
    //The weekday
    String weekday = sdf.format(d);

    int num_events;

    public class obj implements Serializable{
        int a = 5;
        char ba = 'g';
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPrefernces simply stores the number of events. This initializes the storage.
        SharedPreferences shared = this.getSharedPreferences("events",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        for (int i=0;i<7;i++){
            if (!shared.contains(day_string[i]+"_num_events")){
                editor.putInt(day_string[i]+"_num_events",0);
                editor.commit();
            }
        }
        if (!shared.contains("num_events")){
            editor.putInt("num_events",0);
            editor.commit();
        }

        //Log.d("debug","Current date is "+weekday);

        num_events = shared.getInt("num_events",0);
        Log.d("debug","Number of events "+num_events);

        for (int i=0;i<num_events;i++){
            event marked = event.retreivedata(this,"event_"+i);
            if (marked.marked_for_death.length()!=0&&marked.marked_for_death.compareTo(weekday)!=0) {
                event.delete_event(this, i);
                i--;
            }
            if (marked.one_time&&marked.day[event.day_to_ind(weekday)]){
                marked.marked_for_death = weekday;
            }

        }

        Intent intent = getIntent();
        if (intent.hasExtra("date")){
            weekday = intent.getStringExtra("date");
        }

        //Views
        Button add_activity_button = (Button) findViewById(R.id.add_activity);
        add_activity_button.setOnClickListener(this);

        Button delete_events = (Button) findViewById(R.id.delete_events);
        delete_events.setOnClickListener(this);

        Button delete_all = (Button) findViewById(R.id.delete_all);
        delete_all.setOnClickListener(this);

        TextView weekday_textview = (TextView) findViewById(R.id.weekday);
        weekday_textview.setText(weekday);

        LinearLayout event_column = (LinearLayout) findViewById(R.id.event_column);

        ArrayList<event> all_events = new ArrayList<event>();
        int day_ind = event.day_to_ind(weekday);

        for (int i=0;i<num_events;i++){
            event retreived = event.retreivedata(this,"event_"+i);
            if (retreived == null){
                Log.d("debug","uh oh");
                break;
            }
            if (retreived.day[day_ind]) all_events.add(retreived);
        }
        Log.d("debug","Events today "+all_events.size());
        Collections.sort(all_events);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i=0;i<all_events.size();i++){
            TextView class_info = new TextView(this);
            class_info.setLayoutParams(layoutParams);
            event cur_event = all_events.get(i);
            class_info.setGravity(Gravity.CENTER_HORIZONTAL);
            class_info.setTextSize(16);
            class_info.setText("Name: "+cur_event.name+"  Desc: "+cur_event.desc+" Location: "+cur_event.location+'\n'
                    +cur_event.starttime_hour+":");
            if (cur_event.starttime_min<10) class_info.append("0");
            class_info.append(cur_event.starttime_min+" "+cur_event.starttime_am+" - "
                    +cur_event.endtime_hour+":");
            if (cur_event.starttime_min<10) class_info.append("0");
            class_info.append(cur_event.endtime_min+" "+cur_event.endtime_am);
            class_info.setId(cur_event.event_num);
            event_column.addView(class_info);
            //class_info.setId(i);

            LinearLayout buttons = new LinearLayout(this);
            buttons.setOrientation(LinearLayout.HORIZONTAL);
            buttons.setGravity(Gravity.CENTER_HORIZONTAL);

            ImageButton delete = new ImageButton(this);
            delete.setPadding(20,10,20,10);

            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.weight = 1;

            //delete.setLayoutParams(p);
            delete.setImageResource(R.drawable.delete);
            delete.setTag(true);
            delete.setId(cur_event.event_num);
            delete.setOnClickListener(this);
            buttons.addView(delete);

            ImageButton edit = new ImageButton(this);
            edit.setPadding(20,10,20,10);

            //delete.setLayoutParams(p);
            edit.setImageResource(R.drawable.edit);
            edit.setTag(false);
            edit.setId(cur_event.event_num);
            edit.setOnClickListener(this);
            buttons.addView(edit);

            event_column.addView(buttons);
        }
    }

    @Override
    public void onClick(View v) {
        int clicked_id = v.getId();
        if (clicked_id==R.id.add_activity){
            startActivity(new Intent(this, AddEventActivity.class));
        }
        else if (clicked_id==R.id.delete_events){
            event.clear_date(this,weekday);
            startActivity(new Intent(this, MainActivity.class));
        }
        else if (clicked_id==R.id.delete_all){
            for (int i=0;i<num_events;i++){
                event.delete_event(this,0);
            }
            startActivity(new Intent(this, MainActivity.class));
        }
        else{
            ImageButton image_but = (ImageButton) v;
            if ((Boolean)image_but.getTag()){
                event.delete_event(this,clicked_id);
                startActivity(new Intent(this, MainActivity.class));
            }
            else{
                Intent intent = new Intent(this, AddEventActivity.class);
                intent.putExtra("edit","event_"+clicked_id);
                intent.putExtra("number",clicked_id);
                startActivity(intent);
            }
        }
    }

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private float x1,x2;
    @Override
    public boolean onTouchEvent(MotionEvent moveevent){
        switch(moveevent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = moveevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = moveevent.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > SWIPE_MIN_DISTANCE)
                {
                    if (x2>x1){
                        Log.d("debug","swipe right");
                        Intent intent = new Intent(this, MainActivity.class);
                        int current_ind = event.day_to_ind(weekday);
                        if (current_ind==0)current_ind = 6;
                        else current_ind--;
                        intent.putExtra("date",day_string[current_ind]);
                        startActivity(intent);
                    }
                    else {
                        Log.d("debug","swipe left");
                        Intent intent = new Intent(this, MainActivity.class);
                        int current_ind = event.day_to_ind(weekday);
                        if (current_ind==6)current_ind = 0;
                        else current_ind++;
                        intent.putExtra("date",day_string[current_ind]);
                        startActivity(intent);
                    }
                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return true;
    }
}
