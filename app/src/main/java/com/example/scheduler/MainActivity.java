package com.example.scheduler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button add_activity_button = (Button) findViewById(R.id.add_activity);
        add_activity_button.setOnClickListener(this);

        LinearLayout event_layout = (LinearLayout) findViewById(R.id.event_column);
        for (int i=0;i<4;i++){
            TextView class_info  = new TextView(this);
            class_info.setLayoutParams(
                    new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
            class_info.setText("Button " + i);
            class_info.setId(i);
            event_layout.addView(class_info);
            Button options = new Button(this);
            options.setId(i+15);
            options.setText("HI");
            event_layout.addView(options);
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
