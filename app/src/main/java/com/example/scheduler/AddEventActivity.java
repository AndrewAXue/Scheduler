package com.example.scheduler;

import android.content.Intent;
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
            for (int i=0;i<7;i++){
                String buttonID = "event_check_" + i;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                CheckBox day = (CheckBox) findViewById(resID);
                event_days[i] = day.isChecked();
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

            event new_event = new event(event_name,
                                        event_starttime_hour_int,
                                        event_starttime_min_int,
                                        event_starttime_am,
                                        event_endtime_hour_int,
                                        event_endtime_min_int,
                                        event_endtime_am,
                                        event_days,
                                        event_location,
                                        event_desc);

            event_error.setText("DONE");
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
