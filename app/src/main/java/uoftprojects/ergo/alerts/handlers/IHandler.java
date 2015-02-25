package uoftprojects.ergo.alerts.handlers;

import java.util.concurrent.locks.Lock;

import uoftprojects.ergo.metrics.IMetric;

/**
 * Created by H on 2/22/2015.
 */
public interface IHandler {

    public boolean handle(IMetric metric);
}
