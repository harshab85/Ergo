package uoftprojects.ergo.alerts.handlers;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import uoftprojects.ergo.R;
import uoftprojects.ergo.util.BaselineUtil;
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
    private SeekBar seekBar = (SeekBar)ActivityUtil.getMainActivity().findViewById(R.id.tilt_detection);

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

        if(VideoUtil.isExerciseRunning()){
            return false;
        }

        Tilt tilt = null;
        if(metric instanceof Tilt){
            tilt = (Tilt)metric;
        }
        else{
            return false;
        }

        final float tiltAngle = tilt.getValue();
        if(tiltAngle > BaselineUtil.PHONE_FLAT_MAX_ANGLE) {
            if (tiltAngle < BaselineUtil.MIN_TILT_ANGLE || tiltAngle > BaselineUtil.MAX_TILT_ANGLE) {

                final MediaPlayer mediaPlayer = MediaPlayer.create(ActivityUtil.getMainActivity(), R.raw.ergo_tilt_the_device);

                vibrator.vibrate(BaselineUtil.VIBRATION_ALERT_PATTERN, 0);

                ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        VideoUtil.pauseVideo();

                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                seekBar.bringToFront();
                                seekBar.setVisibility(View.VISIBLE);
                                seekBar.setProgress((int)tiltAngle);
                            }
                        });
                        mediaPlayer.start();


                        //Toast.makeText(ActivityUtil.getMainActivity(), "Ideal tilt angle (40 to 70). Current : " + tiltAngle, Toast.LENGTH_SHORT).show();
                    }
                });

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

        ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                seekBar.setVisibility(View.INVISIBLE);
                VideoUtil.resumeVideoWhenPaused();
            }
        });


    }

}
