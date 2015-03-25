package uoftprojects.ergo.alerts.handlers;

import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;

import uoftprojects.ergo.alerts.handlers.baseline.Baseline;
import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.metrics.Tilt;
import uoftprojects.ergo.util.ActivityUtil;
import uoftprojects.ergo.util.VideoUtil;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public class TiltHandler implements IHandler {

    private Vibrator vibrator = null;

    private static TiltHandler INSTANCE = null;

    private TiltHandler(){
        vibrator = (Vibrator) ActivityUtil.getMainActivity().getSystemService(Context.VIBRATOR_SERVICE) ;
    }

    public static TiltHandler getInstance(){
        if(INSTANCE == null){
            INSTANCE = new TiltHandler();
        }

        return INSTANCE;
    }


    public boolean handle(IMetric metric){

        Tilt tilt = null;
        if(metric instanceof Tilt){
            tilt = (Tilt)metric;
        }
        else{
            return false;
        }

        float tiltAngle = tilt.getValue();
        if(tiltAngle > Baseline.PHONE_FLAT_MAX_ANGLE) {
            if (tiltAngle < Baseline.MIN_TILT_ANGLE || tiltAngle > Baseline.MAX_TILT_ANGLE) {
                VideoUtil.pauseVideo();

                String message = "Hold phone at the correct angle (40 to 70). Current: " + tiltAngle;

                vibrator.vibrate(Baseline.VIBRATION_ALERT_PATTERN, 0);
                Toast.makeText(ActivityUtil.getMainActivity(), message, Toast.LENGTH_SHORT).show();

                return true;
            }
            else{
                cancel();
            }
        }
        else{
            cancel();
        }

        return false;
    }

    @Override
    public void cancel() {
        if(vibrator != null){
            vibrator.cancel();
        }

        VideoUtil.resumeVideoWhenPaused();

    }

}
