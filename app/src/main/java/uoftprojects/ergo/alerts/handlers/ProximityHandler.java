package uoftprojects.ergo.alerts.handlers;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import uoftprojects.ergo.R;
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

        System.out.println(proximity.getRectArea());
        // If phone is too close (<25cm), face detection stops. Handles that case
        if(proximity.getRectArea() == 0){
            if(phoneAngle > Baseline.PHONE_MIN_USAGE_ANGLE){
                VideoUtil.pauseVideo();

                // Add splash screen\
                ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView imageView = (ImageView) ActivityUtil.getMainActivity().findViewById(R.id.imageView3);
                        imageView.setVisibility(View.VISIBLE);

                        // play audio
                        MediaPlayer mediaPlayer = MediaPlayer.create(ActivityUtil.getMainActivity(), R.raw.ergo_too_close);
                        mediaPlayer.start();
                    }
                });

                return true;
            }
        }

        // All good so resume video if needed
        cancel();
        return false;
    }

    @Override
    public void cancel() {
        ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView  imageView = (ImageView)ActivityUtil.getMainActivity().findViewById(R.id.imageView3);
                imageView.setVisibility(View.INVISIBLE);
            }
        });
        VideoUtil.resumeVideoWhenPaused();
    }
}
