package uoftprojects.ergo.alerts.handlers.impl.timer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.TimerTask;

import uoftprojects.ergo.R;
import uoftprojects.ergo.alerts.baseline.Baseline;
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

    private Object lock1 = new Object();
    private Object lock2 = new Object();


    private TimerHandler(){
    }

    public static TimerHandler getInstance(){
        if(INSTANCE == null){
            INSTANCE = new TimerHandler();
        }

        return INSTANCE;
    }

    private int count = 2;
    private Bitmap current;


    @Override
    public boolean handle(IMetric metric) {

        if(isRunning){
            Timer.getInstance().resetStartTime();
            return true;
        }

        StartTime startTime = null;
        if(metric instanceof StartTime){
            startTime = (StartTime)metric;
        }
        else{
            return false;
        }

        final long currentTime = System.currentTimeMillis();
        if((currentTime - startTime.getTime()) >= Baseline.MAX_CONTINUOUS_DEVICE_TIME) {

            // Add splash screen\
            ActivityUtil.getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    isRunning = true;

                    Timer.getInstance().resetStartTime();

                    VideoUtil.pauseVideo();

                    // play audio
                    MediaPlayer mediaPlayer = MediaPlayer.create(ActivityUtil.getCurrentActivity(), R.raw.ergo_blink_2);

                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            ActivityUtil.getCurrentActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView textView = (TextView) ActivityUtil.getCurrentActivity().findViewById(R.id.text_placeholder);
                                    textView.setText("You eyes must be getting pretty tired looking at this screen. A quick way to help our eyes stay alert is to blink. By blinking you make tears that keep your eyes refreshed. Let’s bink 30 times together -- follow me.");
                                    textView.setVisibility(View.VISIBLE);

                                    Bitmap b = BitmapFactory.decodeResource(ActivityUtil.getCurrentActivity().getResources(), R.drawable.ergo_alert_2);
                                    Drawable d = ActivityUtil.getCurrentActivity().getResources().getDrawable(R.drawable.ergo_alert_2);
                                    ImageView imageView = (ImageView) ActivityUtil.getCurrentActivity().findViewById(R.id.image_placeholder_1);
                                    imageView.setVisibility(View.VISIBLE);
                                    imageView.setImageBitmap(b);
                                    current = b;
                                }
                            });
                        }
                    });

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                        ActivityUtil.getCurrentActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    final ImageView imageView = (ImageView) ActivityUtil.getCurrentActivity().findViewById(R.id.image_placeholder_1);

                                    final Bitmap b1 = BitmapFactory.decodeResource(ActivityUtil.getCurrentActivity().getResources(), R.drawable.ergo_alert_2);
                                    final Bitmap b2 = BitmapFactory.decodeResource(ActivityUtil.getCurrentActivity().getResources(), R.drawable.ergo_blink_3);

                                    Drawable d1 = ActivityUtil.getCurrentActivity().getResources().getDrawable(R.drawable.ergo_alert_2);
                                    java.util.Timer timer = new java.util.Timer();
                                    Drawable d2 = ActivityUtil.getCurrentActivity().getResources().getDrawable(R.drawable.ergo_blink_3);

                                    timer.scheduleAtFixedRate(new TimerTask() {
                                        @Override
                                        public void run() {

                                            if(count > 0){
                                                count--;
                                            }
                                            else{
                                                isRunning = false;
                                                this.cancel();
                                            }

                                            if(current == b1) {
                                                imageView.setImageBitmap(b2);
                                                current = b2;
                                            }
                                            else{
                                                imageView.setImageBitmap(b1);
                                                current = b1;
                                            }

                                        }
                                    }, 2000, 5000);










                                    //imageView.setImageBitmap(b1);
                                    //imageView.setImageBitmap(b2);
                                    //imageView.setImageBitmap(b1);

                                    //for (int i = 0; i<10; i++) {
                                    /*imageView.setImageDrawable(d2);
                                    sleep(5);*/
                                        /*imageView.setImageDrawable(d1);
                                        sleep(5);*/
                        //            }

                                    //isRunning = false;

                        }
                                                  });
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

    private void sleep(long timeInSec){
        try {
            Thread.sleep(timeInSec*1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancel() {
        ActivityUtil.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView imageView1 = (ImageView) ActivityUtil.getCurrentActivity().findViewById(R.id.image_placeholder_1);
                imageView1.setVisibility(View.INVISIBLE);

                /*ImageView imageView2 = (ImageView) ActivityUtil.getCurrentActivity().findViewById(R.id.image_placeholder_2);
                imageView2.setVisibility(View.INVISIBLE);
*/
                TextView textView = (TextView) ActivityUtil.getCurrentActivity().findViewById(R.id.text_placeholder);
                textView.setVisibility(View.INVISIBLE);

                VideoUtil.resumeVideoWhenPaused();
            }
        });
    }
}
