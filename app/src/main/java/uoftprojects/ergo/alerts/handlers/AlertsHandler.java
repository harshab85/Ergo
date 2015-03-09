package uoftprojects.ergo.alerts.handlers;

import android.view.View;
import android.widget.ImageView;

import uoftprojects.ergo.R;
import uoftprojects.ergo.alerts.handlers.baseline.Baseline;
import uoftprojects.ergo.alerts.handlers.proximity.ProximityHandler;
import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.metrics.Proximity;
import uoftprojects.ergo.metrics.Tilt;
import uoftprojects.ergo.util.ActivityUtil;
import uoftprojects.ergo.util.VideoUtil;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public class AlertsHandler {

    private static AlertsHandler INSTANCE;

    private AlertsHandler(){
    }

    public static AlertsHandler getInstance(){
        if(INSTANCE == null){
            INSTANCE = new AlertsHandler();
        }
        return INSTANCE;
    }

    public IHandler getHandler(IMetric.MetricType type){
        if(type == IMetric.MetricType.Proximity){
            return ProximityHandler.getInstance();
        }
        else if(type == IMetric.MetricType.Tilt){
            return TiltHandler.getInstance();
        }
        else if(type == IMetric.MetricType.Time){
            return TimerHandler.getInstance();
        }

        return null;
    }

    public static void cancelAlerts(){
        ProximityHandler.getInstance().cancel();
        TiltHandler.getInstance().cancel();
        TimerHandler.getInstance().cancel();
    }
}
