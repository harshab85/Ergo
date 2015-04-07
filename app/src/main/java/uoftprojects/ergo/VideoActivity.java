package uoftprojects.ergo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.MediaController;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uoftprojects.ergo.alerts.handlers.AlertsHandler;
import uoftprojects.ergo.engine.SparkPlug;
import uoftprojects.ergo.metrics.usage.MetricsStorage;
import uoftprojects.ergo.rewards.IReward;
import uoftprojects.ergo.rewards.RewardType;
import uoftprojects.ergo.rewards.RewardsHandler;
import uoftprojects.ergo.rewards.RewardsList;
import uoftprojects.ergo.rewards.StickerReward;
import uoftprojects.ergo.util.ActivityUtil;
import uoftprojects.ergo.util.StorageUtil;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public class VideoActivity extends Activity {

    private int duration_msec = 0;
    private RewardsHandler rewardsHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

    @Override
    protected void onPause() {
        VideoView videoView = (VideoView) findViewById(R.id.videoViewMaterial);

        StorageUtil.addInt("video_playback_duration", videoView.getCurrentPosition());

        SparkPlug.stop();
        MetricsStorage.getInstance().store();
        RewardsList.getInstance().store();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        SparkPlug.stop();
        toggleGalleryMode();
    }

    private void initialize() {

        ActivityUtil.setMainActivity(this);
        toggleFullscreen(true);

        String videoFilePath = getIntent().getStringExtra("videoFilePath");
        final VideoView videoView = (VideoView) findViewById(R.id.videoViewMaterial);
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.setVideoPath(videoFilePath);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duration_msec = mp.getDuration();

                int seek = StorageUtil.getInt("video_playback_duration");
                mp.seekTo(seek);
                StorageUtil.addInt("video_playback_duration", 0);

                try {
                    JSONObject currentMetrics = MetricsStorage.getInstance().getCurrentMetrics();
                    rewardsHandler = new RewardsHandler(currentMetrics);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                updateVideoWatchTime(duration_msec);
                duration_msec = 0;
                toggleGalleryMode();
                SparkPlug.stop();

                // Check for rewards at the end of the video
                if(rewardsHandler.shouldUnlockReward()){
                    IReward reward = rewardsHandler.unlock();

                    if(reward == null){
                        Toast.makeText(ActivityUtil.getMainActivity(), "All rewards have been unlocked", Toast.LENGTH_SHORT).show();
                    }
                    else if(reward.getType() == RewardType.Sticker){
                        StickerReward stickerReward = (StickerReward)reward;

                        // TODO Apply sticker to video thumbnails
                        int resourceId = stickerReward.getResourceId();

                        View v = TopActivity.INSTANCE.findViewById(R.id.leftSticker);
                        Drawable drawable = TopActivity.INSTANCE.getResources().getDrawable(resourceId);
                        v.setBackground(drawable);
                        v.setVisibility(View.VISIBLE);

                        //FragmentManager fg = getFragmentManager();
                        //RewardFragment fragment = (RewardFragment) fg.findFragmentById(R.id.fragmentVideoReward);
                        //fragment.getView().setVisibility(View.VISIBLE);

                        System.out.println("Resource: " + resourceId);
                    }
                }
            }
        });

        SparkPlug.start();
        videoView.start();
    }

    private void updateVideoWatchTime(int duration_msec){
        long minutes = 0;
        long seconds = duration_msec/1000;
        if(seconds > 30 && seconds <= 60){
            minutes = 1;
        }
        else{
            minutes = seconds/60;
            long reminder = seconds % 60;
            if(reminder > 30){
                minutes++;
            }
        }

        MetricsStorage.getInstance().updateCurrVideoWatched_Minutes(minutes);
    }


    private void toggleGalleryMode() {
        Intent intent = new Intent(this, TopActivity.class);
        startActivity(intent);
        finish();
    }

    private void toggleFullscreen(boolean fullscreen){
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (fullscreen)
        {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        else
        {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        getWindow().setAttributes(attrs);
    }
}
