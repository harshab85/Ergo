package uoftprojects.ergo.util;

import android.app.Activity;
import android.widget.VideoView;

import uoftprojects.ergo.R;

/**
 * Created by Harsha Balasubramanian on 3/2/2015.
 */
public final class VideoUtil {

    private static boolean PAUSED = false;

    public static void pauseVideo(){
        ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                VideoView videoView = (VideoView) ActivityUtil.getMainActivity().findViewById(R.id.video_playback);
                if (videoView != null && videoView.canPause()) {
                    videoView.pause();
                    PAUSED = true;
                }
            }
        });
    }

    public static void resumeVideoWhenPaused(){
        ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                VideoView videoView = (VideoView) ActivityUtil.getMainActivity().findViewById(R.id.video_playback);
                if(PAUSED && videoView != null && !videoView.isPlaying()) {
                    videoView.start();
                    PAUSED = false;
                }
            }
        });
    }

}