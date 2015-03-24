package uoftprojects.ergo.alerts.handlers.intf;

import uoftprojects.ergo.metrics.IMetric;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public interface IHandler {

    public boolean handle(IMetric metric);

    public void cancel();
}
