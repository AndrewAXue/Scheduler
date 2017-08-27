package com.example.scheduler;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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
import java.util.Comparator;
import java.util.List;


/**
 * Created by Andrew on 8/23/2017.
 */

public class event implements Serializable, Comparable<event>{
    String name;
    int starttime_hour;
    int starttime_min;
    String starttime_am;
    int endtime_hour;
    int endtime_min;
    String endtime_am;
    boolean day[] = new boolean[7];
    String day_string[] = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    String location;
    String desc;
    boolean weekly;
    boolean one_time;

    event(String temp_name,
          int temp_starttime_hour,
          int temp_starttime_min,
          String temp_starttime_am,
          int temp_endtime_hour,
          int temp_endtime_min,
          String temp_endtime_am,
          boolean temp_day[],
          String temp_location,
          String temp_desc,
          boolean temp_weekly,
          boolean temp_one_time){
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
        weekly = temp_weekly;
        one_time = temp_one_time;
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

    public void saveData(Context context){
        for (int i=0;i<7;i++){
            if (day[i]){
                try {
                    SharedPreferences shared = context.getSharedPreferences(day_string[i],Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = shared.edit();
                    int new_key = shared.getInt(day_string[i]+"_num_events",0);
                    edit.putInt(day_string[i]+"_num_events",new_key+1);
                    edit.commit();

                    FileOutputStream fos = context.openFileOutput(day_string[i]+"_event_"+new_key, Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(this);
                    oos.close();
                    fos.close();
                    Log.d("debug","event written to "+day_string[i]+"_event_"+new_key);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("debug","excep");
                }
            }
        }
    }



    public static event retreivedata(Context context, String file_name){
        try {
            //Log.d("debug","retreiving event form "+file_name);
            FileInputStream inp = context.openFileInput(file_name);
            ObjectInputStream ois = new ObjectInputStream(inp);
            event retreived = (event) ois.readObject();
            ois.close();
            inp.close();
            return retreived;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("debug","exception");
        }
        return null;
    }

    public static void clear_date(Context context, String date){
        Log.d("debug","deleting all "+date+" events");
        boolean all_deleted = true;
        SharedPreferences shared = context.getSharedPreferences(date,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shared.edit();
        int new_key = shared.getInt(date+"_num_events",0);
        edit.putInt(date+"_num_events",0);
        edit.commit();

        for (int i=0;i<new_key;i++){
            File file = context.getFileStreamPath(date+"_event_"+i);
            boolean success = file.delete();
            if (!success){
                all_deleted = false;
            }
        }
        if (all_deleted) Log.d("debug","all events deleted");
        else Log.d("debug","OH BOY NOT dEletED");
    }

    public static void delete_event(Context context, String date, int event_num){
        /*
        String[] allfiles = context.fileList();
        for (int i=0;i<allfiles.length;i++){
            Log.d("debug","file  "+allfiles[i]);
        }

        File oldfile = context.getFileStreamPath("Saturday_event_0");
        File newfile = context.getFileStreamPath("Saturday_event_2");
        newfile.renameTo(oldfile);
        Log.d("debug","renamed");

        String[] aallfiles = context.fileList();
        for (int i=0;i<aallfiles.length;i++){
            Log.d("debug","file  "+aallfiles[i]);
        }
        */
        SharedPreferences shared = context.getSharedPreferences(date,Context.MODE_PRIVATE);
        int new_key = shared.getInt(date+"_num_events",0);

        String[] allfiles = context.fileList();
        for (int i=0;i<allfiles.length;i++){
            Log.d("debug",allfiles[i]);
        }

        SharedPreferences.Editor edit = shared.edit();
        edit.putInt(date+"_num_events",new_key-1);
        edit.commit();

        File file = context.getFileStreamPath(date+"_event_"+event_num);
        if (!(event_num == new_key - 1)){
            File rename_file = context.getFileStreamPath(date+"_event_"+(new_key-1));
            rename_file.renameTo(file);
        }
        else{
            file.delete();
        }
    }

    @Override
    public int compareTo(@NonNull event compare_event) {
        int time_one;
        if (this.starttime_am.charAt(0)=='a'&&this.starttime_hour==12){
            time_one = this.starttime_min;
        }
        else{
            time_one = this.starttime_hour*60+this.starttime_min;
        }
        int time_two;
        if (compare_event.starttime_am.charAt(0)=='a'&&compare_event.starttime_hour==12){
            time_two = compare_event.starttime_min;
        }
        else{
            time_two = compare_event.starttime_hour*60+compare_event.starttime_min;
        }
        return time_one-time_two;
    }
}
