package uoftprojects.ergo.alerts.handlers;

import android.widget.Toast;

import uoftprojects.ergo.alerts.handlers.baseline.Baseline;
import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.metrics.StartTime;
import uoftprojects.ergo.sensors.timer.Timer;
import uoftprojects.ergo.util.ActivityUtil;
import uoftprojects.ergo.util.VideoUtil;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public class TimerHandler implements IHandler {

    private static TimerHandler INSTANCE = null;

    private TimerHandler(){
    }

    public static TimerHandler getInstance(){
        if(INSTANCE == null){
            INSTANCE = new TimerHandler();
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
            Toast.makeText(ActivityUtil.getMainActivity(), "Take a break for 5 seconds", Toast.LENGTH_LONG).show();


            // TODO Show exercise here for a few seconds


            Timer.getInstance().resetStartTime();

            return true;
        }
        else{
            return false;
        }
    }
}
