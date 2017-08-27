package com.example.scheduler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

    public class obj implements Serializable{
        int a = 5;
        char ba = 'g';
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Views
        Button add_activity_button = (Button) findViewById(R.id.add_activity);
        add_activity_button.setOnClickListener(this);

        Button delete_events = (Button) findViewById(R.id.delete_events);
        delete_events.setOnClickListener(this);

        TextView weekday_textview = (TextView) findViewById(R.id.weekday);
        weekday_textview.setText(weekday);

        LinearLayout event_column = (LinearLayout) findViewById(R.id.event_column);

        // SharedPrefernces simply stores the number of events. This initializes the storage.
        for (int i=0;i<7;i++){
            SharedPreferences shared = this.getSharedPreferences(day_string[i],Context.MODE_PRIVATE);
            if (!shared.contains(day_string[i]+"_num_events")){
                SharedPreferences.Editor edit = shared.edit();
                edit.putInt(day_string[i]+"_num_events",0);
                edit.commit();
            }
        }

        //Log.d("debug","Current date is "+weekday);

        SharedPreferences read_events = this.getSharedPreferences(weekday,Context.MODE_PRIVATE);
        int num_events = read_events.getInt(weekday+"_num_events",0);
        //Log.d("debug","Number of events today "+num_events);

        event all_events[] = new event[num_events];
        for (int i=0;i<num_events;i++){
            event retreived = event.retreivedata(this,weekday+"_event_"+i);
            if (retreived == null){
                Log.d("debug","uh oh");
                break;
            }
            all_events[i] = retreived;
        }
        Arrays.sort(all_events);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i=0;i<num_events;i++){
            TextView class_info = new TextView(this);
            class_info.setLayoutParams(layoutParams);
            event cur_event = all_events[i];

            class_info.setText("Name: "+cur_event.name+" Desc: "+cur_event.desc);
            class_info.setId(i+1);
            event_column.addView(class_info);
            //class_info.setId(i);

            LinearLayout buttons = new LinearLayout(this);
            buttons.setOrientation(LinearLayout.HORIZONTAL);
            buttons.setGravity(Gravity.CENTER);

            Button delete = new Button(this);
            delete.setGravity(Gravity.CENTER);
            delete.setText("Delete Event");
            delete.setLayoutParams(layoutParams);
            delete.setId(i);
            delete.setOnClickListener(this);
            buttons.addView(delete);

            Button edit = new Button(this);
            edit.setGravity(Gravity.CENTER);
            edit.setText("Edit Event");
            edit.setLayoutParams(layoutParams);
            edit.setId(i);
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
        else{
            event.delete_event(this,weekday,clicked_id);
            startActivity(new Intent(this, MainActivity.class));
        }

    }
}
