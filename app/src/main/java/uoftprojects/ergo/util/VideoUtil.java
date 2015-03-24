package uoftprojects.ergo.util;

import android.app.Activity;
import android.view.Window;
import android.widget.VideoView;
import android.widget.RelativeLayout;

import uoftprojects.ergo.R;

/**
 * Created by Harsha Balasubramanian on 3/2/2015.
 */
public final class VideoUtil {

    public static final String MOVED_TO_BKGRND_FLAG_NAME = "background";

    private static boolean PAUSED = false;

    public static void pauseVideo() {
        ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                VideoView videoView = (VideoView) ActivityUtil.getMainActivity().findViewById(R.id.videoViewMaterial);
                if (videoView != null && videoView.canPause()) {
                    videoView.pause();
                    resize();
                    PAUSED = true;
                }
            }
        });
    }

    public static void resumeVideoWhenPaused() {
        ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyVideoView videoView = (MyVideoView) ActivityUtil.getMainActivity().findViewById(R.id.videoViewMaterial);
                if (PAUSED && videoView != null && !videoView.isPlaying()) {
                    videoView.start();
                    videoView.changeVideoSize(1.0f);
                    PAUSED = false;

                }
            }
        });
    }

    public static void resize() {
        ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyVideoView videoView = (MyVideoView) ActivityUtil.getMainActivity().findViewById(R.id.videoViewMaterial);


                videoView.changeVideoSize(1.25f);


//                RelativeLayout.LayoutParams videoviewlp = new RelativeLayout.LayoutParams(1000, 1000);
//                videoviewlp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//                videoviewlp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
//                videoView.setLayoutParams(videoviewlp);
//                videoView.setScaleX(1.0f);
//                videoView.setScaleY(1.0f);
//                videoView.invalidate();

                //videoView.onMeasure(10,10);
            }
        });
    }


    public static void setMovedToBkgrnd() {
        StorageUtil.addLocalFlag(MOVED_TO_BKGRND_FLAG_NAME, true);
    }

    public static boolean fromBackGround() {
        return StorageUtil.getFlag(MOVED_TO_BKGRND_FLAG_NAME);
    }

}
