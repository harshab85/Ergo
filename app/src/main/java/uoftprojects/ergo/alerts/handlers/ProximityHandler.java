package uoftprojects.ergo.alerts.handlers;

import android.widget.Toast;

import uoftprojects.ergo.alerts.handlers.baseline.Baseline;
import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.metrics.Proximity;
import uoftprojects.ergo.metrics.Tilt;
import uoftprojects.ergo.util.ActivityUtil;
import uoftprojects.ergo.util.VideoUtil;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public class ProximityHandler implements IHandler {

    private static ProximityHandler INSTANCE = null;

    private ProximityHandler(){
    }

    public static ProximityHandler getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ProximityHandler();
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

        IMetric tilt = proximity.getTilt();
        float phoneAngle = 0;
        if(tilt instanceof Tilt){
            phoneAngle = ((Tilt)tilt).getValue();
        }

        // If phone is too close (<25cm), face detection stops. Handles that case
        if(!proximity.detectedFace()){
            if(phoneAngle > Baseline.PHONE_MIN_USAGE_ANGLE){
                VideoUtil.pauseVideo();
                Toast.makeText(ActivityUtil.getMainActivity(), "Too close to face", Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        // if face gets detected, check rect baseline
        if(proximity.detectedFace()) {
            long rectArea = proximity.getRectArea();

            if (rectArea > Baseline.MAX_RECT_AREA) {
                VideoUtil.pauseVideo();
                Toast.makeText(ActivityUtil.getMainActivity(), "Too close to face", Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        // All good so resume video if needed
        VideoUtil.resumeVideoWhenPaused();
        return false;
    }
}
