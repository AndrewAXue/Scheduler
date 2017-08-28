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
import java.util.HashMap;
import java.util.List;


/**
 * Created by Andrew on 8/23/2017.
 */

public class event implements Serializable, Comparable<event>{
    int event_num;
    String name;
    int starttime_hour;
    int starttime_min;
    String starttime_am;
    int endtime_hour;
    int endtime_min;
    String endtime_am;
    boolean day[] = new boolean[7];
    static String day_string[] = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    String location;
    String desc;
    boolean weekly;
    boolean one_time;
    String marked_for_death = "";

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
        try {
            SharedPreferences shared = context.getSharedPreferences("events",Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = shared.edit();
            int new_key = shared.getInt("num_events",0);
            edit.putInt("num_events",new_key+1);
            edit.commit();

            for (int i=0;i<7;i++){
                if (day[i]){
                    edit.putInt(day_string[i]+"_num_events",shared.getInt(day_string[i]+"_num_events",0)+1);
                    edit.commit();
                }
            }
            this.event_num = new_key;

            FileOutputStream fos = context.openFileOutput("event_"+new_key, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
            Log.d("debug","event written to "+"event_"+new_key);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("debug","excep");
        }
    }



    public static event retreivedata(Context context, String file_name){
        try {
            Log.d("debug","retreiving event from "+file_name);
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
        int day_ind = event.day_to_ind(date);

        SharedPreferences shared = context.getSharedPreferences("events",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shared.edit();
        int new_key = shared.getInt("num_events",0);

        for (int i=0;i<new_key;i++){
            event cur_event = event.retreivedata(context,"event_"+i);
            if (cur_event.day[day_ind]){
                delete_event(context,i);
                i--;
                new_key--;
            }
        }
        edit.putInt("num_events",new_key);
        edit.commit();
    }

    public static void delete_event(Context context, int event_num){
        SharedPreferences shared = context.getSharedPreferences("events",Context.MODE_PRIVATE);
        int new_key = shared.getInt("num_events",0);
        SharedPreferences.Editor edit = shared.edit();
        edit.putInt("num_events",new_key-1);
        edit.commit();

        event retreived = event.retreivedata(context,"event_"+event_num);
        for (int i=0;i<7;i++){
            if (retreived.day[i]){
                edit.putInt(day_string[i]+"_num_events",shared.getInt(day_string[i]+"_num_events",0)-1);
                edit.commit();
            }
        }

        File file = context.getFileStreamPath("event_"+event_num);
        if (!(event_num == new_key - 1)){
            File rename_file = context.getFileStreamPath("event_"+(new_key-1));
            rename_file.renameTo(file);

            event rename = event.retreivedata(context,"event_"+event_num);
            rename.event_num = event_num;

            try {
                FileOutputStream fos = context.openFileOutput("event_"+event_num, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(rename);
                oos.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            file.delete();
        }
        Log.d("debug","event_"+event_num+" deleted");
    }

    @Override
    public int compareTo(@NonNull event compare_event) {
        int time_one;
        if (this.starttime_am.charAt(0)=='a'&&this.starttime_hour==12){
            time_one = this.starttime_min;
        }
        else{
            time_one = this.starttime_hour*60+this.starttime_min;
            if (this.starttime_am.charAt(0)=='p'){
                time_one+=60*12;
            }
        }
        int time_two;
        if (compare_event.starttime_am.charAt(0)=='a'&&compare_event.starttime_hour==12){
            time_two = compare_event.starttime_min;
        }
        else{
            time_two = compare_event.starttime_hour*60+compare_event.starttime_min;
            if (compare_event.starttime_am.charAt(0)=='p'){
                time_two+=60*12;
            }
        }
        return time_one-time_two;
    }

    public static int day_to_ind(String date){
        int day_ind = -1;
        if (date.charAt(0)=='M'){
            return 0;
        }
        else if (date.charAt(2)=='e'){
            return 1;
        }
        else if (date.charAt(0)=='W'){
            return 2;
        }
        else if (date.charAt(0)=='T'){
            return 3;
        }
        else if (date.charAt(0)=='F'){
            return 4;
        }
        else if (date.charAt(2)=='t'){
            return 5;
        }
        else if (date.charAt(0)=='S'){
            return 6;
        }
        Log.d("debug","could not find date");
        return 0;
    }
}
