package com.csc2514.rsvpexperiment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.csc2514.rsvpexperiment.utils.Csv;
import com.csc2514.rsvpexperiment.utils.OffsetWord;
import com.csc2514.rsvpexperiment.utils.Rsvp;
import com.csc2514.rsvpexperiment.utils.TimerFactory;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

/**
 * Created by Andrés on 3/14/2015.
 */
public class RsvpReadingDemo extends ReadingActivity {

    TimerFactory timerFactory;
    Rsvp rsvp;
    int wait;
    int offset = 5;
    int wpm = 250;
    EditText text;
    TextView coloredLetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        try {
            changeText(R.raw.text0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        timerFactory = new TimerFactory();
        text =  (EditText)findViewById(R.id.text);
        coloredLetter = (TextView) findViewById(R.id.coloredLetter);
        coloredLetter.setText("");
        findViewById(R.id.text1).setVisibility(View.GONE);
        findViewById(R.id.text2).setVisibility(View.GONE);
        findViewById(R.id.username).setVisibility(View.GONE);
    }

    @Override
    public void start(View view) throws IOException, InterruptedException {
        int id = 1;
        if(((RadioButton) findViewById(R.id.text1)).isChecked()){
            id = R.raw.text1;
        }else{
            id = R.raw.text2;
        }
        String fileContents = readFile(getResources().openRawResource(id));
        rsvp = new Rsvp(fileContents,wpm,offset);
        wait = rsvp.calculateMillisecondsWord();
        timerFactory.start(new TimeTask(),wait);
        disableCommands();
    }

    @Override
    public void stop() {
        timerFactory.stop();
    }

    @Override
    public void processResults()  {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("RSVP finished!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getBaseContext(), Home.class);
                        startActivity(intent);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    class TimeTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (rsvp.hasNext()){
                        OffsetWord word = rsvp.next();
                        String print = "";
                        String offsetWhite = (word.getOffset() > 0) ? String.format("%"+word.getOffset()+"s", " ") : "";
                        print+="________________\n";
                        print+=String.format("%"+offset+"s", " ")+"|\n";

                        String pre = (word.getPre().length() > 0) ? String.format("%"+word.getPre().length()+"s", " ") : "";
                        coloredLetter.setText(offsetWhite + pre + word.getFocus());
                        print+= offsetWhite + "" + word + "\n";

                        print+=String.format("%"+offset+"s", " ").replace(" ","_")+"|__________\n";
                        final String finalPrint = print;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text.setText(finalPrint);
                            }
                        });
                        //System.out.println(print);
                        //Thread.sleep(wait);
                    }else{
                        stop();
                        processResults();
                        coloredLetter.setText("");
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