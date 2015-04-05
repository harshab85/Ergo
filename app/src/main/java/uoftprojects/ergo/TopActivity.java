package uoftprojects.ergo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.GridView;
import android.widget.MediaController;
import android.widget.Toast;
import android.view.View;
import android.view.WindowManager;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.widget.Toolbar;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import uoftprojects.ergo.alerts.handlers.AlertsHandler;
import uoftprojects.ergo.engine.SparkPlug;
import uoftprojects.ergo.metrics.usage.MetricsStorage;
import uoftprojects.ergo.rewards.IReward;
import uoftprojects.ergo.rewards.RewardType;
import uoftprojects.ergo.rewards.RewardsHandler;
import uoftprojects.ergo.rewards.RewardsList;
import uoftprojects.ergo.rewards.StickerReward;
import uoftprojects.ergo.util.ActivityUtil;
import uoftprojects.ergo.util.SetupUtil;
import uoftprojects.ergo.util.StorageUtil;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by ryanprimeau on 15-03-22.
 */
public class TopActivity extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        VideoView videoView = (VideoView) findViewById(R.id.videoViewMaterial);

        ActivityUtil.setMainActivity(this);

        videoView.setVisibility(View.INVISIBLE);

        toolbar.setTitle("Ergo Video Gallery");

        //SharedPreferences sharedPreferences = getSharedPreferences("ErgoSetup", 0);


        if(!SetupUtil.isSetupCompeted()){//sharedPreferences != null) {
            //boolean setupCompleted = sharedPreferences.getBoolean("setupCompleted", false);
            //if (!setupCompleted) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            //}
        }

        String storedMetrics = StorageUtil.getString(MetricsStorage.STORAGE_KEY);
        if(storedMetrics != null && !storedMetrics.isEmpty()){
            MetricsStorage.getInstance().initialize(storedMetrics);
        }

        initialize();


    }

    private void initialize() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also nextReward example)

//        String[] mDataset = new String[10];
//
//        for(int i = 0 ; i < mDataset.length; i++){
//            mDataset[i] = "Title:"+i;
//        }


        List<VideoInfo> videos = loadVideos(); //,"Batman","Elmo","Shrek","Bugs Life","Frozen"};

        mAdapter = new MyAdapter(videos);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {


                        if (cursor.moveToPosition(position)) {
                            int fileColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                            String videoFilePath = cursor.getString(fileColumn);

                            VideoView videoView = (VideoView) toggleVideoMode();
                            MediaController mediaController = new MediaController(ActivityUtil.getMainActivity());

                            videoView.setMediaController(mediaController);
                            videoView.setVideoPath(videoFilePath);

                            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    duration_msec = mp.getDuration();
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

                                            System.out.println("Resource: " + resourceId);
                                        }
                                    }
                                }
                            });

                            SparkPlug.start();
                            videoView.start();
                            //requestWindowFeature(Window.FEATURE_NO_TITLE);
                        }


                    }
                })
        );
    }

    private int duration_msec = 0;
    private RewardsHandler rewardsHandler = null;


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

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

    @Override
    protected void onPause() {
        SparkPlug.stop();
        MetricsStorage.getInstance().store();
        RewardsList.getInstance().store();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        toggleGalleryMode();
        SparkPlug.stop();
        MetricsStorage.getInstance().toString();
    }

    private void toggleFullscreen(boolean fullscreen)
    {
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


    private View toggleVideoMode() {
        VideoView videoView = (VideoView) findViewById(R.id.videoViewMaterial);
        videoView.setVisibility(View.VISIBLE);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.my_recycler_view);
        recycler.setVisibility(View.INVISIBLE);

        toggleFullscreen(true);

        return videoView;
    }

    private View toggleGalleryMode() {
        VideoView videoView = (VideoView) findViewById(R.id.videoViewMaterial);
        videoView.setVisibility(View.INVISIBLE);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.my_recycler_view);
        recycler.setVisibility(View.VISIBLE);

        toggleFullscreen(false);
        return recycler;
    }


    private Cursor cursor;
    private List<VideoInfo> loadVideos(){
        List<VideoInfo> videos = new ArrayList<>();

        String[] thumbColumns = {
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID,
        };

        String[] mediaColumns = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.MIME_TYPE };

        cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                VideoInfo videoInfo = new VideoInfo();

                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));

                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

                videoInfo.displayName = displayName;
                videoInfo.filePath = filePath;


                ContentResolver crThumb = getContentResolver();
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inSampleSize = 1;
                Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
                System.out.println();
                if(curThumb != null) {
                    videoInfo.thumbPath = String.valueOf(curThumb);
                }


                Cursor thumbCursor = managedQuery(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                + "=" + id, null, null);

                if (thumbCursor.moveToFirst()) {
                    videoInfo.thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                    System.out.println(videoInfo.thumbPath);
                }


                videos.add(videoInfo);


            } while (cursor.moveToNext());
        }


        return videos;
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }






}
