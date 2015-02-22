package uoftprojects.ergo.alerts.handlers;

import android.app.Activity;

import uoftprojects.ergo.metrics.IMetric;

/**
 * Created by H on 2/22/2015.
 */
public class AlertsHandler {

    private static AlertsHandler INSTANCE;

    private Activity activity;

    private AlertsHandler(Activity activity){
        this.activity = activity;
    }

    public static AlertsHandler getInstance(Activity activity){
        if(INSTANCE == null){
            INSTANCE = new AlertsHandler(activity);
        }
        return INSTANCE;
    }

    public IHandler getHandler(IMetric.MetricType type){
        if(type == IMetric.MetricType.Proximity){
            return ProximityHandler.getInstance(activity);
        }
        else if(type == IMetric.MetricType.Tilt){
            return TiltHandler.getInstance(activity);
        }
        else if(type == IMetric.MetricType.Time){
            return TimerHandler.getInstance(activity);
        }

        return null;
    }
}
