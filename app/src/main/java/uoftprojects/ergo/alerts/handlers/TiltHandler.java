package uoftprojects.ergo.alerts.handlers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import uoftprojects.ergo.R;
import uoftprojects.ergo.metrics.usage.MetricsStorage;
import uoftprojects.ergo.sensors.timer.Timer;
import uoftprojects.ergo.util.BaselineUtil;
import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.metrics.Tilt;
import uoftprojects.ergo.util.ActivityUtil;
import uoftprojects.ergo.util.StorageUtil;
import uoftprojects.ergo.util.VideoUtil;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public class TiltHandler implements IHandler {

    private Vibrator vibrator = null;
    private int errorCount = 0;
    private boolean threshold;


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

            if (tiltAngle < BaselineUtil.MIN_TILT_ANGLE || tiltAngle > BaselineUtil.MAX_TILT_ANGLE) {

                errorCount++;
                VideoUtil.pause();


                if(errorCount < 5) {

                    final MediaPlayer mediaPlayer = MediaPlayer.create(ActivityUtil.getMainActivity(), R.raw.ergo_tilt_the_device);
                    ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    Matrix mat = new Matrix();
                                    Bitmap bMap = BitmapFactory.decodeResource(ActivityUtil.getMainActivity().getResources(), R.mipmap.ic_launcher);
                                    mat.postRotate(-90);
                                    Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), mat, true);
                                    Drawable d = new BitmapDrawable(ActivityUtil.getMainActivity().getResources(), bMapRotate);

                                    seekBar.setThumb(d);
                                    seekBar.setProgress((int) tiltAngle);
                                    seekBar.bringToFront();
                                    seekBar.setVisibility(View.VISIBLE);

                                    vibrator.vibrate(BaselineUtil.VIBRATION_ALERT_PATTERN, 0);

                                    mediaPlayer.start();
                                }
                            });

                        }
                    });
                }
                else{

                    threshold = true;

                    // Show Text
                    ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(!VideoUtil.isTiltVideoRunning()) {
                                VideoUtil.setIsTiltVideoRunning(true);
                                seekBar.setVisibility(View.INVISIBLE);
                            }

                            // Pick a tilt video
                            String path = "android.resource://" + ActivityUtil.getMainActivity().getPackageName() + "/";
                            if (tiltAngle < BaselineUtil.MIN_TILT_ANGLE) {
                                path += R.raw.tilt_toward_you;
                            }
                            else{
                                path += R.raw.tilt_away_from_you;
                            }

                            VideoView view = (VideoView)ActivityUtil.getMainActivity().findViewById(R.id.video_playback);
                            view.setVisibility(View.VISIBLE);
                            view.bringToFront();
                            view.setVideoURI(Uri.parse(path));

                            view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp.start();
                                }
                            });

                            if(!VideoUtil.getTiltErrorVideo().equals(path)){
                                VideoUtil.setTiltErrorVideo(path);
                                view.start();
                            }
                        }
                    });

                }

                return true;
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

        errorCount = 0;

        ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                seekBar.setVisibility(View.INVISIBLE);

                if(VideoUtil.isTiltVideoRunning()){
                    VideoUtil.setIsTiltVideoRunning(false);

                    VideoView view = (VideoView)ActivityUtil.getMainActivity().findViewById(R.id.video_playback);
                    view.setVisibility(View.INVISIBLE);
                    if(view.isPlaying()) {
                        view.stopPlayback();
                    }
                }

                boolean neededResume = VideoUtil.resume();
                if(neededResume && threshold){
                    MetricsStorage.getInstance().updateCurrTiltErrors();
                    threshold = false;
                }

            }
        });
    }

}
