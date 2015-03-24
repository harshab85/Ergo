package uoftprojects.ergo.alerts.handlers.intf;

import uoftprojects.ergo.alerts.sensorhandlers.ProximityHandler;
import uoftprojects.ergo.alerts.sensorhandlers.TiltHandler;
import uoftprojects.ergo.metrics.IMetric;

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
        /*else if(type == IMetric.MetricType.Time){
            return TimerHandler.getInstance();
        }*/

        return null;
    }

    public static void cancelAlerts(){
        ProximityHandler.getInstance().cancel();
        TiltHandler.getInstance().cancel();
    }

}
