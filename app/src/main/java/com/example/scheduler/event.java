package com.example.scheduler;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Created by Andrew on 8/23/2017.
 */

public class event {
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

    public void saveData(Context context,String file_name,event object) throws IOException{
        Log.d("debug","saving event");
        FileOutputStream fos = context.openFileOutput(file_name, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        event tst = new event();
        oos.writeObject(tst);
        oos.close();
        fos.close();
        Log.d("debug","saved event");
    }
}
