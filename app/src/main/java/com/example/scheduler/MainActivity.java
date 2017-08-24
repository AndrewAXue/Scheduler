package com.example.scheduler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] choices = {"am","pm","HIHIHI"};

        AutoCompleteTextView autoComplete = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoComplete.setThreshold(1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, choices);
        autoComplete.setAdapter(adapter);


        Button add_activity_button = (Button) findViewById(R.id.add_activity);
        add_activity_button.setOnClickListener(this);

        LinearLayout event_layout = (LinearLayout) findViewById(R.id.event_column);
        try {
            FileInputStream events = openFileInput("TEST");
            ObjectInputStream inp = new ObjectInputStream(events);
            event temp = (event)inp.readObject();

            TextView class_info  = new TextView(this);
            class_info.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

            class_info.setText(temp.name);
            //class_info.setId(0);
            event_layout.addView(class_info);

            Button options = new Button(this);
            //options.setId(i+15);
            options.setText(temp.location);
            event_layout.addView(options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int clicked_id = v.getId();
        if (clicked_id==R.id.add_activity){
            startActivity(new Intent(this, AddEventActivity.class));
        }
    }
}
