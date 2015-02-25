package uoftprojects.ergo.alerts.handlers;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;

import uoftprojects.ergo.alerts.handlers.baseline.Baseline;
import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.metrics.StartTime;
import uoftprojects.ergo.sensors.timer.Timer;

/**
 * Created by H on 2/22/2015.
 */
public class TimerHandler implements IHandler {

    private Activity activity;

    private static TimerHandler INSTANCE = null;

    private TimerHandler(Activity activity){
        this.activity = activity;
    }

    public static TimerHandler getInstance(Activity activity){
        if(INSTANCE == null){
            INSTANCE = new TimerHandler(activity);
        }

        return INSTANCE;
    }



    @Override
    public boolean handle(IMetric metric) {
        StartTime startTime = null;
        if(metric instanceof StartTime){
            startTime = (StartTime)metric;
        }
        else{
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if((currentTime - startTime.getTime()) >= Baseline.MAX_CONTINUOUS_DEVICE_TIME) {
            Toast.makeText(activity, "Take a break", Toast.LENGTH_LONG).show();
            Timer.getInstance().resetStartTime();
            return true;
        }

        return false;
    }
}
