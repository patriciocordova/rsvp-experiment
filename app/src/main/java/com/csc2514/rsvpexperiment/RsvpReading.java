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
public class RsvpReading extends ReadingActivity {

    TimerFactory timerFactory;
    Rsvp rsvp;
    int wait;
    int offset = 5;
    int wpm = 220;
    EditText text;
    TextView coloredLetter;

    String name;
    int typeExperiment;
    int textOrder;
    String startTime;
    String endTime;
    int textId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        try {
            changeText(R.raw.text1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        timerFactory = new TimerFactory();
        text =  (EditText)findViewById(R.id.text);
        coloredLetter = (TextView) findViewById(R.id.coloredLetter);
        coloredLetter.setText("");
        TextView tvTitle = (TextView)findViewById(R.id.title);
        tvTitle.setText("RSVP");

        //Get extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("Name");
            typeExperiment = extras.getInt("TypeExperiment");
            textOrder = extras.getInt("TextOrder");
            TextView tv = (TextView) findViewById(R.id.username);
            tv.setText(name);

            if(typeExperiment == 0){
                startTime = extras.getString("StartTime");
                endTime = extras.getString("EndTime");
            }

            textId = -1;
            if(typeExperiment == 1){
                textId = (textOrder == 0)?R.raw.text1:R.raw.text2;
            }else{
                textId = (textOrder == 0)?R.raw.text2:R.raw.text1;
            }

            try {
                changeText(textId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start(View view) throws IOException, InterruptedException {
        int id = textId;
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
        if(typeExperiment == 1){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("RSVP finished!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getBaseContext(), NormalReading.class);
                            intent.putExtra("TypeExperiment", typeExperiment);
                            intent.putExtra("TextOrder", textOrder);
                            intent.putExtra("Name", name);
                            startActivity(intent);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Experiment finished! Save data?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Csv fileResults = new Csv(new File(getFilesDir(), Home.fileName));
                            try {
                                String sTextOrder = (textOrder == 0)?"AB":"BA";
                                String sTypeExperiment = (typeExperiment == 0)?"SR":"RS";
                                fileResults.add(new String[]{name, startTime+" | "+endTime, "Done",sTypeExperiment,sTextOrder});
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
                    if (rsvp.hasNext()){
                        OffsetWord word = rsvp.next();
                        String print = "";
                        String offsetWhite = (word.getOffset() > 0) ? String.format("%"+word.getOffset()+"s", " ") : "";
                        print+="_____________\n";
                        print+=String.format("%"+offset+"s", " ")+"|\n";

                        String pre = (word.getPre().length() > 0) ? String.format("%"+word.getPre().length()+"s", " ") : "";
                        coloredLetter.setText(offsetWhite + pre + word.getFocus());
                        print+= offsetWhite + "" + word + "\n";

                        print+=String.format("%"+offset+"s", " ").replace(" ","_")+"|_______\n";
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