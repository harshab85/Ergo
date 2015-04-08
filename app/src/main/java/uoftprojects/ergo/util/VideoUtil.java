package uoftprojects.ergo.util;

import android.view.View;
import android.widget.VideoView;

import uoftprojects.ergo.R;

/**
 * Created by Harsha Balasubramanian on 3/2/2015.
 */
public final class VideoUtil {

    private static boolean PAUSED = false;

    private static boolean isExerciseRunning = false;

    private static boolean isTiltVideoRunning = false;

    private static String tiltErrorVideo = "";

    public static void pauseVideo() {
        VideoView videoView = (VideoView) ActivityUtil.getMainActivity().findViewById(R.id.videoViewMaterial);
        if (videoView != null && videoView.canPause()) {
            videoView.pause();
            PAUSED = true;
        }
    }

    public static void pause() {
        VideoView videoView = (VideoView) ActivityUtil.getMainActivity().findViewById(R.id.videoViewMaterial);
        if (videoView != null && videoView.canPause()) {
            videoView.setVisibility(View.INVISIBLE);

            if(StorageUtil.getInt("videoSeekTime") <= 0) {
                StorageUtil.addInt("videoSeekTime", videoView.getCurrentPosition());
            }

            videoView.pause();
            PAUSED = true;
        }
    }

    public static boolean resumeVideoWhenPaused() {
        VideoView videoView = (VideoView) ActivityUtil.getMainActivity().findViewById(R.id.videoViewMaterial);
        if (PAUSED && videoView != null && !videoView.isPlaying()) {
            videoView.start();
            PAUSED = false;

            return true;
        }

        return false;
    }

    public static boolean resume() {
        VideoView videoView = (VideoView) ActivityUtil.getMainActivity().findViewById(R.id.videoViewMaterial);
        if (PAUSED && videoView != null && !videoView.isPlaying()) {
            videoView.setVisibility(View.VISIBLE);
            videoView.bringToFront();

            int seek = (StorageUtil.getInt("videoSeekTime") <= 0) ? 0:StorageUtil.getInt("videoSeekTime");
            System.out.println("Seek : " + seek);
            videoView.seekTo(seek);
            videoView.start();
            PAUSED = false;

            return true;
        }

        return false;
    }

    public static void resize(final float size) {

        ActivityUtil.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyVideoView videoView = (MyVideoView) ActivityUtil.getMainActivity().findViewById(R.id.videoViewMaterial);
                videoView.changeVideoSize(size);
            }
        });
    }


    public static void setIsTiltVideoRunning(boolean isRunning) {
        isTiltVideoRunning = isRunning;
    }

    public static boolean isTiltVideoRunning() {
        return isTiltVideoRunning;
    }

    public static void setTiltErrorVideo(String video) {
        tiltErrorVideo = video;
    }

    public static String getTiltErrorVideo() {
        return tiltErrorVideo;
    }

    public static boolean isExerciseRunning(){
        return isExerciseRunning;
    }

    public static void setIsExerciseRunning(boolean isRunning){
        isExerciseRunning = isRunning;
    }

}
