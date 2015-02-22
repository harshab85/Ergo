package uoftprojects.ergo.alerts.handlers;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;

import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.metrics.Tilt;

/**
 * Created by H on 2/22/2015.
 */
public class TiltHandler implements IHandler {

    private Activity activity;

    private Vibrator vibrator = null;

    private static TiltHandler INSTANCE = null;

    private TiltHandler(Activity activity){
        vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE) ;
        this.activity = activity;
    }

    public static TiltHandler getInstance(Activity activity){
        if(INSTANCE == null){
            INSTANCE = new TiltHandler(activity);
        }

        return INSTANCE;
    }


    public void handle(IMetric metric){

        Tilt tilt = null;
        if(metric instanceof Tilt){
            tilt = (Tilt)metric;
        }
        else{
            return;
        }

        float tiltAngle = tilt.getValue();
        if(tiltAngle > 0) {
            long[] pattern = new long[]{100, 50};
            if (tiltAngle < 40 || tiltAngle > 60) {
                vibrator.vibrate(pattern, 0);
                Toast.makeText(activity, "Hold phone at the correct angle", Toast.LENGTH_SHORT).show();
            }
            else{
                vibrator.cancel();
            }
        }
    }

}
