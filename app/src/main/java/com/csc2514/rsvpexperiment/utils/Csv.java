package com.csc2514.rsvpexperiment.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Csv{

    File file;
    ArrayList<String[]> data;

    public Csv(File file){
        this.file = file;
    }

    public void add(String[] data) throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
        bw.append(TextUtils.join(",", data)+"\n");
        bw.close();
    }

    public void remove(int lineNumber) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String currentLine;
        String newFile = "";
        int cont = 0;
        while((currentLine = reader.readLine()) != null) {
            if(cont++ == lineNumber) continue;
            newFile += currentLine+"\n";
        }
        reader.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(newFile);
        writer.flush();
        writer.close();
    }

    public void removeAll() throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("");
        writer.flush();
        writer.close();
    }

    public ArrayList<String[]> read() throws IOException{
        if(data != null){
            return data;
        }
        data = new ArrayList<String[]>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            data.add(tokenize(line));
        }
        reader.close();
        return data;
    }

    public String[] tokenize(String line){
        return line.split(",");
    }
}