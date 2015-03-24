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

                    PAUSED = false;

                }
            }
        });
    }

    public static void resize(float size) {

        final float whyisthisrequired = size;
        ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyVideoView videoView = (MyVideoView) ActivityUtil.getMainActivity().findViewById(R.id.videoViewMaterial);
                videoView.changeVideoSize(whyisthisrequired);
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
