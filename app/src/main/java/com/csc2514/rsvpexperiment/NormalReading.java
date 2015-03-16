package com.csc2514.rsvpexperiment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csc2514.rsvpexperiment.utils.Csv;
import com.csc2514.rsvpexperiment.utils.TimerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;


public class NormalReading extends ReadingActivity {

    TimerFactory timerFactory;
    SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    int seconds;
    String username;
    String type;
    String startTime;
    String endTime;

    private static final int HIDER_FLAGS = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //getWindow().getDecorView().setSystemUiVisibility(HIDER_FLAGS);
        setContentView(R.layout.activity_reading);
        try {
            changeText(R.raw.text1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        seconds = totalSeconds;
        timerFactory = new TimerFactory();

        //Get extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("Name");
            type = extras.getString("Type");
            TextView tv = (TextView) findViewById(R.id.username);
            tv.setText(username);
        }
    }

    @Override
    public void start(View view) {
        startTime = sdf.format(new Date());
        seconds = totalSeconds;
        Toast toast = Toast.makeText(getApplicationContext(), seconds + " seconds left", Toast.LENGTH_SHORT);
        toast.show();
        timerFactory.start(new TimeTask(),1000);
        disableCommands();
    }

    @Override
    public void stop() {
        timerFactory.stop();
        // Save results
    }

    @Override
    public void processResults()  {
        endTime = sdf.format(new Date());
        final int normalReadingTime = totalSeconds - seconds;
        if(type.compareTo("AB") == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Infinite scrolling finished!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getBaseContext(), RsvpReading.class);
                        intent.putExtra("Type", type);
                        intent.putExtra("Name", username);
                        intent.putExtra("StartTime", startTime);
                        intent.putExtra("EndTime", endTime);
                        startActivity(intent);
                    }
                });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Experiment finished! Save data?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id){
                            Csv fileResults = new Csv(new File(getFilesDir(),Home.fileName));
                            try {
                                fileResults.add(new String[]{username,startTime+" | "+endTime,"Done",type});
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(getBaseContext(), Home.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getBaseContext(), Home.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    class TimeTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (seconds == 20 || seconds == 10 || seconds == 2) {
                        Toast toast = Toast.makeText(getApplicationContext(), seconds + " seconds left", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    if(seconds > 0){
                        seconds--;
                    }else{
                        stop();
                        processResults();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        stop();
        super.onBackPressed();
        return;
    }
}
