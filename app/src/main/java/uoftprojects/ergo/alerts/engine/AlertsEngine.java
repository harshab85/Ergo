package uoftprojects.ergo.alerts.engine;

import android.app.Activity;

import java.util.List;

import uoftprojects.ergo.alerts.handlers.AlertsHandler;
import uoftprojects.ergo.alerts.handlers.IHandler;
import uoftprojects.ergo.metrics.IMetric;

/**
 * Created by H on 2/22/2015.
 */
public class AlertsEngine {

    private Activity mainActivity;

    public AlertsEngine(Activity mainActivity){
        this.mainActivity = mainActivity;
    }

    public void testWithBaseLine(List<IMetric> metricsList){
        if(metricsList != null){
            for(IMetric metric: metricsList){
                IHandler handler = AlertsHandler.getInstance(mainActivity).getHandler(metric.getType());
                if(handler != null) {
                    handler.handle(metric);
                }
            }
        }
    }
}
