package uoftprojects.ergo.alerts.handlers;

import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import uoftprojects.ergo.R;
import uoftprojects.ergo.alerts.handlers.baseline.Baseline;
import uoftprojects.ergo.engine.SparkPlug;
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

    private int videoPlayCount = 0;
    private int videoSeekTime = 0;

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

        if(VideoUtil.isExerciseRunning()){
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

            // Show Text
            ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    VideoUtil.setIsExerciseRunning(true);
                    VideoUtil.pauseVideo();

                    // Setup blinking video
                    String path = "android.resource://" + ActivityUtil.getMainActivity().getPackageName() + "/" + R.raw.blinking_7;
                    final VideoView view = (VideoView)ActivityUtil.getMainActivity().findViewById(R.id.video_playback);
                    view.setVisibility(View.VISIBLE);
                    view.bringToFront();
                    view.setVideoURI(Uri.parse(path));
                    view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if(videoPlayCount < 10) {
                                mp.start();
                                videoPlayCount++;
                            }
                            else{
                                Timer.getInstance().resetStartTime();
                                VideoUtil.setIsExerciseRunning(false);

                                VideoView view2 = (VideoView)ActivityUtil.getMainActivity().findViewById(R.id.videoViewMaterial);
                                view2.setVisibility(View.VISIBLE);
                                view2.seekTo(videoSeekTime);

                                videoSeekTime = 0;
                                videoPlayCount = 0;
                            }
                        }
                    });

                    // Play instruction audio
                    MediaPlayer mediaPlayer = MediaPlayer.create(ActivityUtil.getMainActivity(), R.raw.blinking_6);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            TextView textView = (TextView) ActivityUtil.getMainActivity().findViewById(R.id.text_placeholder);
                            textView.setVisibility(View.VISIBLE);
                            textView.bringToFront();
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            VideoView view2 = (VideoView)ActivityUtil.getMainActivity().findViewById(R.id.videoViewMaterial);
                            videoSeekTime = view2.getCurrentPosition();
                            view2.setVisibility(View.INVISIBLE);
                            view.start();
                        }
                    });
                    mediaPlayer.start();

                }
            });

            return true;
        }
        else{
            cancel();
            return false;
        }
    }

    @Override
    public void cancel() {

        // Show Text
        ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = (TextView) ActivityUtil.getMainActivity().findViewById(R.id.text_placeholder);
                textView.setVisibility(View.INVISIBLE);

                VideoView view = (VideoView)ActivityUtil.getMainActivity().findViewById(R.id.video_playback);
                view.setVisibility(View.INVISIBLE);

                VideoUtil.resumeVideoWhenPaused();
            }
        });
    }
}
