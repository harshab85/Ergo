package uoftprojects.ergo.alerts.handlers;

import android.app.Activity;

import uoftprojects.ergo.metrics.IMetric;

/**
 * Created by H on 2/22/2015.
 */
public class ProximityHandler implements IHandler {

    private Activity activity;

    private static ProximityHandler INSTANCE = null;

    private ProximityHandler(Activity activity){
        this.activity = activity;
    }

    public static ProximityHandler getInstance(Activity activity){
        if(INSTANCE == null){
            INSTANCE = new ProximityHandler(activity);
        }

        return INSTANCE;
    }

    @Override
    public void handle(IMetric metric) {

    }
}
