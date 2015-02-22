package uoftprojects.ergo.alerts.handlers;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;

import uoftprojects.ergo.metrics.IMetric;

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
    public void handle(IMetric metric) {

    }
}
