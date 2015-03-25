package uoftprojects.ergo.alerts.handlers;

import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import uoftprojects.ergo.R;
import uoftprojects.ergo.alerts.handlers.baseline.Baseline;
import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.metrics.Tilt;
import uoftprojects.ergo.util.ActivityUtil;
import uoftprojects.ergo.util.VideoUtil;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public class TiltHandler implements IHandler {

    //private Vibrator vibrator = null;

    private static TiltHandler INSTANCE = null;
    private SeekBar seekBar = (SeekBar)ActivityUtil.getMainActivity().findViewById(R.id.tilt_detection);
    //private int videoSeekTime;

    private TiltHandler(){
        //vibrator = (Vibrator) ActivityUtil.getMainActivity().getSystemService(Context.VIBRATOR_SERVICE) ;
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
        if(tiltAngle > Baseline.PHONE_FLAT_MAX_ANGLE) {
            if (tiltAngle < Baseline.MIN_TILT_ANGLE || tiltAngle > Baseline.MAX_TILT_ANGLE) {

                ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        VideoUtil.pauseVideo();

                        /*VideoView view2 = (VideoView)ActivityUtil.getMainActivity().findViewById(R.id.videoViewMaterial);
                        videoSeekTime = view2.getCurrentPosition();*/
                        //view2.setVisibility(View.INVISIBLE);

                        

                        seekBar.setVisibility(View.VISIBLE);
                        //seekBar.setEnabled(false);
                        seekBar.bringToFront();
                        seekBar.setProgress((int)tiltAngle);
                        seekBar.setAlpha(1f);

                        Toast.makeText(ActivityUtil.getMainActivity(), "Ideal tilt angle (40 to 70). Current : " + tiltAngle, Toast.LENGTH_SHORT).show();
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
        ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                seekBar.setVisibility(View.INVISIBLE);
                /*VideoView view2 = (VideoView)ActivityUtil.getMainActivity().findViewById(R.id.videoViewMaterial);
                view2.setVisibility(View.VISIBLE);*/

                /*if(videoSeekTime > 0) {
                    view2.seekTo(videoSeekTime);
                    videoSeekTime = 0;
                }*/

                VideoUtil.resumeVideoWhenPaused();
            }
        });


    }

}
