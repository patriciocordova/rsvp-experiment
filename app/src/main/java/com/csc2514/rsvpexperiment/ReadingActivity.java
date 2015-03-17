package com.csc2514.rsvpexperiment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public abstract class ReadingActivity extends Activity {

    int totalSeconds;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeText(int id) throws IOException {
        String text1 = readFile(getResources().openRawResource(id));
        EditText textWidget = (EditText) findViewById(R.id.text);
        textWidget.setText(text1);

        switch(id) {
            case R.raw.text1:
                totalSeconds = 70;
                break;
            case R.raw.text2:
                totalSeconds = 64;
                break;
        }
    }

    public String readFile(InputStream name) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(name));
        String contents = "";
        String line;
        while ((line = br.readLine()) != null) {
            contents += line + "\n\n";
        }
        br.close();
        br = null;
        return contents;
    }

    public void disableCommands(){
        Button b = (Button) findViewById(R.id.start);
        b.setEnabled(false);
    }

    abstract void start(View view) throws IOException, InterruptedException;
    abstract void stop();
    abstract void processResults() throws IOException;
}
