package uoftprojects.ergo.alerts.handlers.impl.timer;

import android.view.View;
import android.widget.ImageView;

import uoftprojects.ergo.R;
import uoftprojects.ergo.alerts.baseline.Baseline;
import uoftprojects.ergo.alerts.handlers.intf.IHandler;
import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.metrics.StartTime;
import uoftprojects.ergo.util.ActivityUtil;
import uoftprojects.ergo.util.VideoUtil;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public class TimerHandler implements IHandler {

    private static TimerHandler INSTANCE = null;

    private TimerHandler(){
    }

    public static TimerHandler getInstance(){
        if(INSTANCE == null){
            INSTANCE = new TimerHandler();
        }

        return INSTANCE;
    }

    @Override
    public boolean handle(IMetric metric) {
        StartTime startTime = null;
        if(metric instanceof StartTime){
            startTime = (StartTime)metric;
        }
        else{
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if((currentTime - startTime.getTime()) >= Baseline.MAX_CONTINUOUS_DEVICE_TIME) {
            //Toast.makeText(ActivityUtil.getMainActivity(), "Timer ran out (60 seconds). Replace this and show an eye exercise", Toast.LENGTH_LONG).show();

            // Show exercise here for a few seconds
            VideoUtil.pauseVideo();
            //AlertsHandler.cancelAlerts();

            ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*LinearLayout exerciseOverlay = (LinearLayout)ActivityUtil.getMainActivity().findViewById(R.id.exercise_overlay);
                    exerciseOverlay.setVisibility(View.VISIBLE);*/

                    // Embed random exercise

                    ImageView imageView = (ImageView) ActivityUtil.getMainActivity().findViewById(R.id.imageView3);
                    imageView.setVisibility(View.VISIBLE);

                    //exerciseOverlay.setVisibility(View.GONE);
                }
            });

            //cancel();

            //Timer.getInstance().resetStartTime();

            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void cancel() {
        VideoUtil.resumeVideoWhenPaused();
    }
}
