package com.csc2514.rsvpexperiment.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Andrés on 3/14/2015.
 */
public class TimerFactory {

    Timer timer;

    public void start(TimerTask task, int time){
        if(timer == null){
            timer = new Timer();
        }

        timer.schedule(task, 0, time);
    }

    public void stop() {
        if(timer != null){
            this.timer.cancel();
            this.timer.purge();
            this.timer = null;
        }
    }
}
