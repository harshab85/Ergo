package uoftprojects.ergo.alerts.handlers;

import uoftprojects.ergo.metrics.IMetric;

/**
 * Created by H on 2/22/2015.
 */
public interface IHandler {

    public void handle(IMetric metric);
}
