package uoftprojects.ergo.alerts.handlers;

import android.app.Activity;
import android.widget.Toast;

import uoftprojects.ergo.alerts.handlers.baseline.Baseline;
import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.metrics.Proximity;

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
    public boolean handle(IMetric metric) {

        Proximity proximity = null;
        if(metric instanceof Proximity){
            proximity = (Proximity)metric;
        }
        else{
            return false;
        }

        // TODO improve using rect area
        /*if(!proximity.didDetectFace()) {
            Toast.makeText(activity, "Too close to face", Toast.LENGTH_SHORT).show();
            return true;
        }*/

        if(proximity.didDetectFace()){
            long rectArea = proximity.getRectArea();
            if(rectArea < Baseline.MIN_RECT_AREA || rectArea > Baseline.MAX_RECT_AREA){
                // TODO proximity alert
                Toast.makeText(activity, "Too close to face", Toast.LENGTH_SHORT).show();
            }
        }

        return false;
    }
}
