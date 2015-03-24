package uoftprojects.ergo.alerts.handlers.impl.timer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;

import uoftprojects.ergo.R;
import uoftprojects.ergo.alerts.baseline.Baseline;
import uoftprojects.ergo.alerts.handlers.impl.timer.exercises.impl.ExerciseHandler;
import uoftprojects.ergo.alerts.handlers.intf.IHandler;
import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.metrics.StartTime;
import uoftprojects.ergo.sensors.timer.Timer;
import uoftprojects.ergo.util.ActivityUtil;
import uoftprojects.ergo.util.VideoUtil;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public class TimerHandler implements IHandler {

    private static TimerHandler INSTANCE = null;

    private boolean isRunning;

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

        if(isRunning){
            return true;
        }


        StartTime startTime = null;
        if(metric instanceof StartTime){
            startTime = (StartTime)metric;
        }
        else{
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if((currentTime - startTime.getTime()) >= Baseline.MAX_CONTINUOUS_DEVICE_TIME) {
            Timer.getInstance().resetStartTime();

            VideoUtil.pauseVideo();

            // Add splash screen\
            ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    isRunning = true;

                    final Bitmap bm1 = BitmapFactory.decodeResource(ActivityUtil.getMainActivity().getResources(), R.drawable.ergo_alert_2);
                    final Bitmap bm2 = BitmapFactory.decodeResource(ActivityUtil.getMainActivity().getResources(), R.drawable.ergo_blink_3);


                    ImageView imageView = (ImageView) ActivityUtil.getMainActivity().findViewById(R.id.proximity_alert_image);
                    imageView.setImageBitmap(bm2);
                    imageView.setVisibility(View.VISIBLE);

                    // play audio
                    MediaPlayer mediaPlayer = MediaPlayer.create(ActivityUtil.getMainActivity(), R.raw.ergo_blink);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            ImageView imageView = (ImageView) ActivityUtil.getMainActivity().findViewById(R.id.proximity_alert_image);

                            for(int i=0; i<2; i++){
                                try {
                                    imageView.setImageResource(0);
                                    imageView.setImageBitmap(bm1);
                                    imageView.setVisibility(View.VISIBLE);

                                    Thread.sleep(1500);

                                    imageView.setImageResource(0);
                                    imageView.setImageBitmap(bm2);
                                    imageView.setVisibility(View.VISIBLE);

                                    Thread.sleep(1500);
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            isRunning = false;
                        }
                    });

                    mediaPlayer.start();
                }
            });

            return true;
        }

        cancel();
        return false;
    }

    @Override
    public void cancel() {
        ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView imageView = (ImageView)ActivityUtil.getMainActivity().findViewById(R.id.proximity_alert_image);
                imageView.setVisibility(View.INVISIBLE);
            }
        });
        VideoUtil.resumeVideoWhenPaused();
    }
}
