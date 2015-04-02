package uoftprojects.ergo.alerts.handlers;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;

import uoftprojects.ergo.R;
import uoftprojects.ergo.util.BaselineUtil;
import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.metrics.Proximity;
import uoftprojects.ergo.metrics.Tilt;
import uoftprojects.ergo.util.ActivityUtil;
import uoftprojects.ergo.util.VideoUtil;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public class ProximityHandler implements IHandler {

    private double factor = 1;
    private static ProximityHandler INSTANCE = null;
    private Vibrator vibrator = null;


    private ProximityHandler(){
        vibrator = (Vibrator) ActivityUtil.getMainActivity().getSystemService(Context.VIBRATOR_SERVICE) ;

    }

    public static ProximityHandler getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ProximityHandler();
        }

        return INSTANCE;
    }

    @Override
    public boolean handle(IMetric metric) {

        if(VideoUtil.isExerciseRunning()){
            return false;
        }

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

        if(proximity.getRectArea() == 0){
            if(phoneAngle > BaselineUtil.PHONE_MIN_USAGE_ANGLE){

                ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        VideoUtil.pauseVideo();

                        vibrator.vibrate(BaselineUtil.VIBRATION_ALERT_PATTERN, 0);

                        // play audio
                        MediaPlayer mediaPlayer = MediaPlayer.create(ActivityUtil.getMainActivity(), R.raw.ergo_too_close);
                        mediaPlayer.start();

                        if(factor > BaselineUtil.MAX_ZOOM_FACTOR) {
                            ImageView imageView = (ImageView) ActivityUtil.getMainActivity().findViewById(R.id.imageView3);
                            imageView.setVisibility(View.VISIBLE);
                            imageView.bringToFront();
                        }
                        else {
                            factor = factor + 0.2;
                            VideoUtil.resize((float) factor);
                        }
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

        if(vibrator != null){
            vibrator.cancel();
        }

        ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            ImageView  imageView = (ImageView)ActivityUtil.getMainActivity().findViewById(R.id.imageView3);
            imageView.setVisibility(View.INVISIBLE);

                VideoUtil.resize(1f);
                VideoUtil.resumeVideoWhenPaused();

                factor = 1;
            }
        });

    }
}
