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

    int totalSeconds = 70;

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

    public void onRadioButtonClicked(View view) throws IOException {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        Context context = getApplicationContext();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.text1:
                if (checked) {
                    changeText(R.raw.text1);
                    totalSeconds = 70;
                    Toast toast = Toast.makeText(context, "Text A", Toast.LENGTH_SHORT);
                    toast.show();
                    stop();
                }
                break;
            case R.id.text2:
                if (checked) {
                    changeText(R.raw.text2);
                    totalSeconds = 56;
                    Toast toast = Toast.makeText(context, "Text B", Toast.LENGTH_SHORT);
                    toast.show();
                    stop();
                }
                break;
        }
    }

    public void changeText(int id) throws IOException {
        String text1 = readFile(getResources().openRawResource(id));
        EditText textWidget = (EditText) findViewById(R.id.text);
        textWidget.setText(text1);

        switch(id) {
            case R.id.text1:
                totalSeconds = 70;
                break;
            case R.id.text2:
                totalSeconds = 56;
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
        RadioButton rb1 = (RadioButton) findViewById(R.id.text1);
        RadioButton rb2 = (RadioButton) findViewById(R.id.text2);
        Button b = (Button) findViewById(R.id.start);
        rb1.setEnabled(false);
        rb2.setEnabled(false);
        b.setEnabled(false);
    }

    abstract void start(View view) throws IOException, InterruptedException;
    abstract void stop();
    abstract void processResults() throws IOException;
}
