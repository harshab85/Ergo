package uoftprojects.ergo.engine;

import android.app.Activity;

import java.util.List;

import uoftprojects.ergo.alerts.handlers.AlertsHandler;
import uoftprojects.ergo.alerts.handlers.IHandler;
import uoftprojects.ergo.metrics.IMetric;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public class AlertsEngine {

    private static IMetric.MetricType currentAlert_MetricType = null;

    public AlertsEngine(){
    }

    public void testWithBaseLine(List<IMetric> metricsList){
        if(metricsList != null){
            for(IMetric metric: metricsList){

                if(currentAlert_MetricType != null ){
                    if(metric.getType() != currentAlert_MetricType){
                        continue;
                    }
                }

                handleCurrentAlert(metric);
            }
        }
    }

    private void handleCurrentAlert(IMetric metric){
        IHandler handler = AlertsHandler.getInstance().getHandler(metric.getType());
        if(handler != null) {
            boolean isAlertTriggered = handler.handle(metric);
            if(isAlertTriggered){
                currentAlert_MetricType = metric.getType();
            }
            else{
                currentAlert_MetricType = null;
            }
        }
    }
}