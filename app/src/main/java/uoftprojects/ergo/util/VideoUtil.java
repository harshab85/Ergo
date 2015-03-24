package uoftprojects.ergo.util;

import android.widget.VideoView;

import uoftprojects.ergo.R;

/**
 * Created by Harsha Balasubramanian on 3/2/2015.
 */
public final class VideoUtil {

    public static final String MOVED_TO_BKGRND_FLAG_NAME = "background";

    private static boolean PAUSED = false;

    public static void pauseVideo() {
        VideoView videoView = (VideoView) ActivityUtil.getCurrentActivity().findViewById(R.id.video_playback);
        if (videoView != null && videoView.canPause()) {
            videoView.pause();
            PAUSED = true;
        }
    }

    public static void resumeVideoWhenPaused() {
        VideoView videoView = (VideoView) ActivityUtil.getCurrentActivity().findViewById(R.id.video_playback);
        if (PAUSED && videoView != null && !videoView.isPlaying()) {
            videoView.start();
            PAUSED = false;
        }
    }

    public static void setMovedToBkgrnd() {
        StorageUtil.addLocalFlag(MOVED_TO_BKGRND_FLAG_NAME, true);
    }

    public static boolean fromBackGround() {
        return StorageUtil.getFlag(MOVED_TO_BKGRND_FLAG_NAME);
    }

}
