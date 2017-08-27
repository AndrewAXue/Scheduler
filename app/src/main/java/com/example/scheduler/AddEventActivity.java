package com.example.scheduler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class AddEventActivity extends AppCompatActivity implements View.OnClickListener{
    String day_string[] = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Button event_add_button = (Button) findViewById(R.id.event_add_button);
        event_add_button.setOnClickListener(this);

        String[] choices = {"am","pm"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, choices);

        AutoCompleteTextView event_endtime_am = (AutoCompleteTextView) findViewById(R.id.event_endtime_am);
        event_endtime_am.setThreshold(1);
        event_endtime_am.setAdapter(adapter);

        AutoCompleteTextView event_starttime_am = (AutoCompleteTextView) findViewById(R.id.event_starttime_am);
        event_starttime_am.setThreshold(1);
        event_starttime_am.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent.hasExtra("edit")){
            String file_name = intent.getStringExtra("edit");
            event edit_event = event.retreivedata(this,file_name);

            EditText event_name = (EditText)findViewById(R.id.event_name);
            event_name.setText(edit_event.name);

            EditText event_starttime_hour = (EditText)findViewById(R.id.event_name);
            event_starttime_hour.setText(edit_event.starttime_hour);

            EditText event_starttime_min = (EditText)findViewById(R.id.event_name);
            event_starttime_min.setText(edit_event.starttime_min);

            event_starttime_am.setText(edit_event.starttime_hour);

            EditText event_endtime_hour = (EditText)findViewById(R.id.event_name);
            event_endtime_hour.setText(edit_event.endtime_hour);

            EditText event_endtime_min = (EditText)findViewById(R.id.event_name);
            event_endtime_min.setText(edit_event.endtime_min);

            event_endtime_am.setText(edit_event.starttime_hour);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id==R.id.event_add_button){
            TextView event_error = (TextView) findViewById(R.id.event_error);

            String event_name = ((EditText) findViewById(R.id.event_name)).getText().toString();
            if (event_name.length()==0){
                event_error.setText("ERROR: REQUIRES NAME");
                return;
            }

            String event_starttime_hour = ((EditText) findViewById(R.id.event_starttime_hour)).getText().toString();
            String event_starttime_min = ((EditText) findViewById(R.id.event_starttime_min)).getText().toString();

            int event_starttime_hour_int = Integer.valueOf(event_starttime_hour);
            int event_starttime_min_int = Integer.valueOf(event_starttime_min);

            if (event_starttime_hour.length()==0){
                event_error.setText("ERROR: REQUIRES START HOUR");
                return;
            }
            if (event_starttime_hour.length()>2||event_starttime_hour_int>12){
                event_error.setText("ERROR: INVALID START HOUR");
                return;
            }
            if (event_starttime_min.length()==0){
                event_error.setText("ERROR: REQUIRES START MINUTE");
                return;
            }
            if (event_starttime_min.length()>2||event_starttime_min_int>60){
                event_error.setText("ERROR: INVALID START MINUTE");
                return;
            }


            String event_endtime_hour = ((EditText) findViewById(R.id.event_endtime_hour)).getText().toString();
            String event_endtime_min = ((EditText) findViewById(R.id.event_endtime_min)).getText().toString();

            int event_endtime_hour_int = Integer.valueOf(event_endtime_hour);
            int event_endtime_min_int = Integer.valueOf(event_endtime_min);

            if (event_endtime_hour.length()==0){
                event_error.setText("ERROR: REQUIRES END HOUR");
                return;
            }
            if (event_endtime_hour.length()>2||event_endtime_hour_int>12){
                event_error.setText("ERROR: INVALID END HOUR");
                return;
            }
            if (event_endtime_min.length()==0){
                event_error.setText("ERROR: REQUIRES END MINUTE");
                return;
            }
            if (event_endtime_min.length()>2||event_endtime_min_int>60){
                event_error.setText("ERROR: INVALID END MINUTE");
                return;
            }

            boolean event_days[] = new boolean[7];
            boolean date_chosen = false;
            for (int i=0;i<7;i++){
                String buttonID = "event_check_" + i;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                CheckBox day = (CheckBox) findViewById(resID);
                event_days[i] = day.isChecked();
                if (event_days[i]){
                    date_chosen = true;
                }
            }
            if (!date_chosen){
                event_error.setText("ERROR: NO DATES CHOSEN");
                return;
            }

            String event_endtime_am = ((AutoCompleteTextView) findViewById(R.id.event_endtime_am)).getText().toString();
            String event_starttime_am = ((AutoCompleteTextView) findViewById(R.id.event_starttime_am)).getText().toString();

            if (event_endtime_am.compareTo("am")!=0&&event_endtime_am.compareTo("pm")!=0){
                event_error.setText("ERROR: INVALID END AM/PM");
                return;
            }
            if (event_starttime_am.compareTo("am")!=0&&event_starttime_am.compareTo("pm")!=0){
                event_error.setText("ERROR: INVALID START AM/PM");
                return;
            }

            String event_location = ((EditText) findViewById(R.id.event_location)).getText().toString();
            String event_desc = ((EditText) findViewById(R.id.event_desc)).getText().toString();

            boolean weekly = ((CheckBox)findViewById(R.id.event_weekly)).isChecked();
            boolean one_time = ((CheckBox)findViewById(R.id.event_one_time)).isChecked();

            event new_event;

            if (weekly&&one_time){
                event_error.setText("CAN'T BE WEEKLY AND ONE TIME");
                return ;
            }
            if (!(weekly||one_time)){
                event_error.setText("MUST BE WEEKLY OR ONE TIME");
                return ;
            }

            for (int i=0;i<7;i++){
                SharedPreferences shared = this.getSharedPreferences(day_string[i], Context.MODE_PRIVATE);
                if (shared.getInt(day_string[i]+"_num_events",0)>=10){
                    event_error.setText("TOO MANY EVENTS ON "+day_string[i]+" ALREADY");
                    return;
                }
            }

            new_event = new event(event_name,
                    event_starttime_hour_int,
                    event_starttime_min_int,
                    event_starttime_am,
                    event_endtime_hour_int,
                    event_endtime_min_int,
                    event_endtime_am,
                    event_days,
                    event_location,
                    event_desc,
                    weekly,
                    one_time);

            event_error.setText("DONE");
            new_event.saveData(this);

            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
