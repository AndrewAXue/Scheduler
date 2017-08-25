package com.example.scheduler;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public class obj implements Serializable{
        int a = 5;
        char ba = 'g';
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button add_activity_button = (Button) findViewById(R.id.add_activity);
        add_activity_button.setOnClickListener(this);

        LinearLayout event_layout = (LinearLayout) findViewById(R.id.event_column);

        event tst = new event();
        tst.saveData(this,"test name");
    }

    @Override
    public void onClick(View v) {
        int clicked_id = v.getId();
        if (clicked_id==R.id.add_activity){
            startActivity(new Intent(this, AddEventActivity.class));
        }
    }
}
