package com.example.scheduler;

import android.content.Context;
import android.util.Log;

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


/**
 * Created by Andrew on 8/23/2017.
 */

public class event implements Serializable{
    String name;
    int starttime_hour;
    int starttime_min;
    String starttime_am;
    int endtime_hour;
    int endtime_min;
    String endtime_am;
    boolean day[] = new boolean[7];
    String location;
    String desc;

    event(String temp_name,
          int temp_starttime_hour,
          int temp_starttime_min,
          String temp_starttime_am,
          int temp_endtime_hour,
          int temp_endtime_min,
          String temp_endtime_am,
          boolean temp_day[],
          String temp_location,
          String temp_desc){
        name = temp_name;
        starttime_hour = temp_starttime_hour;
        starttime_min = temp_starttime_min;
        starttime_am = temp_starttime_am;
        endtime_hour = temp_endtime_hour;
        endtime_min = temp_endtime_min;
        endtime_am = temp_endtime_am;
        for (int i=0;i<7;i++){
            day[i] = temp_day[i];
        }
        location = temp_location;
        desc = temp_desc;
    }

    event(){
        name = "test name";
        starttime_hour = 12;
        starttime_min = 00;
        starttime_am = "am";
        endtime_hour = 12;
        endtime_min = 0;
        endtime_am = "am";
        for (int i=0;i<7;i++){
            if (i!=3&&i!=6)day[i] = true;
            else day[i]=false;

        }
        location = "test loca";
        desc = "test desc";
    }

    public void saveData(Context context,String file_name){
        try {

            FileOutputStream fos = context.openFileOutput(file_name, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
            Log.d("debug","event saved");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("debug","excep");
        }
    }
}
