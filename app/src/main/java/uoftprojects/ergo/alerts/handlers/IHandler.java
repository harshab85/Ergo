package uoftprojects.ergo.alerts.handlers;

import uoftprojects.ergo.metrics.IMetric;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public interface IHandler {

    public boolean handle(IMetric metric);
}
