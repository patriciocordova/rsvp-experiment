package com.csc2514.rsvpexperiment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.csc2514.rsvpexperiment.utils.Csv;
import com.csc2514.rsvpexperiment.utils.Rsvp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Home extends Activity {

    static String fileName = "experiment_results.csv";
    int lastIndex;
    int typeExperiment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        lastIndex = 0;
        try {
            recreateTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newExperiment(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] array = {"SR", "RS","Demo"};
        builder.setTitle("New experiment")
                .setSingleChoiceItems(array, 1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                            }
                        })
                .setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        typeExperiment = lw.getCheckedItemPosition();

                        if (typeExperiment != 2) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                            CharSequence[] array = {"Text A", "Text B"};
                            builder.setTitle("First text")
                                    .setSingleChoiceItems(array, 1,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int which) {
                                                }
                                            })
                                    .setPositiveButton("Go", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            ListView lw = ((AlertDialog) dialog).getListView();
                                            int textOrder = lw.getCheckedItemPosition();
                                            if (typeExperiment == 0) {
                                                Intent intent = new Intent(getBaseContext(), NormalReading.class);
                                                intent.putExtra("TypeExperiment", typeExperiment);
                                                intent.putExtra("TextOrder", textOrder);
                                                intent.putExtra("Name", "User " + lastIndex);
                                                startActivity(intent);
                                            } else{
                                                Intent intent = new Intent(getBaseContext(), RsvpReading.class);
                                                intent.putExtra("TypeExperiment", typeExperiment);
                                                intent.putExtra("TextOrder", textOrder);
                                                intent.putExtra("Name", "User " + lastIndex);
                                                startActivity(intent);
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });
                            AlertDialog dialog2 = builder.create();
                            dialog2.show();
                        }else{
                            Intent intent = new Intent(getBaseContext(), RsvpReadingDemo.class);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void recreateTable() throws IOException {
        TableLayout table = (TableLayout) findViewById(R.id.table);
        table.removeAllViews();

        table.addView(createRow(new String[]{"User","Infinite\nScrolling","RSVP","Exp.","Order"},true,false));
        Csv fileResults = new Csv(new File(getFilesDir(),fileName));
        ArrayList<String[]> results = fileResults.read();
        for (int i=0;i<results.size();i++){
            String[] currentRow = results.get(i);
            table.addView(createRow(currentRow,false,true));
            if(i+1 >= results.size()){
                lastIndex = Integer.parseInt(currentRow[0].split(" ")[1]) + 1;
            }
        }
    }

    public TableRow createRow(String[] data, boolean isTitle,boolean checkThirdResult){
        TableRow row = new TableRow(this);
        for (int i=0;i<data.length;i++){
            TextView tv = new TextView(this);
            String text = data[i];
            if(checkThirdResult && i==1){
                String[] dates = text.split("[ |]+");
                text = dates[1] + " -\n" + dates[3];
            }
            tv.setText(text);
            if(isTitle) {
                tv.setTextSize(16);
                //tv.setTypeface(null, Typeface.BOLD);
            }
            tv.setWidth(210);
            tv.setTextColor(Color.WHITE);
            TableRow innerRow = new TableRow(this);
            innerRow.addView(tv);
            row.addView(innerRow);
        }
        return row;
    }

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

    public void deleteAll(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete all?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                        builder.setMessage("Are you sure?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        Csv fileResults = new Csv(new File(getFilesDir(), fileName));
                                        try {
                                            fileResults.removeAll();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        lastIndex = 0;
                                        try {
                                            recreateTable();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        AlertDialog dialog2 = builder.create();
                        dialog2.show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
