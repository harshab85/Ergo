package uoftprojects.ergo.sensors.timer;

import uoftprojects.ergo.metrics.StartTime;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public class Timer {

    private static Timer INSTANCE = new Timer();

    private long startTime;

    private Timer(){
        startTime = System.currentTimeMillis();
    }

    public static Timer getInstance(){
        return INSTANCE;
    }

    public StartTime getStartTime(){
        return new StartTime(this.startTime);
    }

    public void resetStartTime(){
        startTime = System.currentTimeMillis();
    }
}
