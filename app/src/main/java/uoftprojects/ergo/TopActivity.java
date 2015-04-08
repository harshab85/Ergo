package uoftprojects.ergo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.opengl.Visibility;
import android.os.Bundle;
import android.app.Fragment;

import android.app.FragmentManager;

import android.view.ViewOutlineProvider;
import android.graphics.Outline;


import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.View;
import android.view.WindowManager;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.File;


import android.widget.Toolbar;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

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
import org.json.JSONObject;
import org.json.JSONException;


/**
 * Created by ryanprimeau on 15-03-22.
 */
public class TopActivity extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private int duration_msec = 0;
    private RewardsHandler rewardsHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ergo");
        toolbar.setLogo(R.mipmap.ic_launcher);
        setActionBar(toolbar);
        ActivityUtil.setMainActivity(this);

        FragmentManager fg = getFragmentManager();
        RewardFragment fragment = (RewardFragment) fg.findFragmentById(R.id.fragmentVideoReward);
        fragment.getView().setVisibility(View.INVISIBLE);


        View addButton = findViewById(R.id.add_button);
        addButton.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int diameter = getResources().getDimensionPixelSize(R.dimen.diameter);
                outline.setOval(0, 0, diameter, diameter);
            }
        });
        addButton.setClipToOutline(true);


        VideoView videoView = (VideoView) findViewById(R.id.videoViewMaterial);


        videoView.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPreferences = getSharedPreferences("ErgoSetup", 0);

        if(!SetupUtil.isSetupCompeted()){//sharedPreferences != null) {
            //boolean setupCompleted = sharedPreferences.getBoolean("setupCompleted", false);
            //if (!setupCompleted) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }

        String storedMetrics = StorageUtil.getString(MetricsStorage.METRICS_STORAGE_KEY);
        if(storedMetrics != null && !storedMetrics.isEmpty()){
            MetricsStorage.getInstance().initialize(storedMetrics);
        }

        try{
            JSONObject storedMetricsJSON = new JSONObject(storedMetrics);
            rewardsHandler = new RewardsHandler(storedMetricsJSON);
        }catch (Exception e){

        }




        //       initializeForTutorials();
        initialize();
//
    }

    RecyclerItemClickListener one;
    RecyclerItemClickListener two;
    private void initialize() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //notifyDataSetChanged(mRecylerView);

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
        mRecyclerView.removeOnItemTouchListener(one);
        mRecyclerView.removeOnItemTouchListener(two);



        String storedMetrics = StorageUtil.getString(MetricsStorage.METRICS_STORAGE_KEY);
        if(storedMetrics != null && !storedMetrics.isEmpty()){
            MetricsStorage.getInstance().initialize(storedMetrics);
            JSONObject storedMetricsJSON = null;
            try {
                storedMetricsJSON = new JSONObject(storedMetrics);
            }
            catch (JSONException e) {
                // Ignore
            }
            rewardsHandler = new RewardsHandler(storedMetricsJSON);
        }
        else{
            rewardsHandler = new RewardsHandler();
        }

        if(rewardsHandler == null) {
            Toast.makeText(ActivityUtil.getMainActivity(), "NULL REWARDS HANDLER", Toast.LENGTH_SHORT).show();

        }

        one =  new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {


                if (cursor.moveToPosition(position)) {
                    int fileColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                    String videoFilePath = cursor.getString(fileColumn);

                    final VideoView videoView = (VideoView) toggleVideoMode();
                    MediaController mediaController = new MediaController(ActivityUtil.getMainActivity());

                    videoView.setMediaController(mediaController);
                    videoView.setVideoPath(videoFilePath);


                    // Store video watched
                    

                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            updateVideoWatchTime(duration_msec);
                            duration_msec = 0;
                            toggleGalleryMode();
                            SparkPlug.stop();

                            View v2 = view.findViewById(R.id.rewardSticker);
                            v2.setVisibility(View.VISIBLE);

                            // Check for rewards at the end of the video
                            if(rewardsHandler.shouldUnlockReward()){
                                IReward reward = rewardsHandler.unlock();

                                if(reward == null){
                                    Toast.makeText(ActivityUtil.getMainActivity(), "All rewards have been unlocked", Toast.LENGTH_SHORT).show();
                                }
                                else if(reward.getType() == RewardType.Sticker){
                                    StickerReward stickerReward = (StickerReward)reward;

                                    int resourceId = stickerReward.getResourceId();
                                    View v = view.findViewById(R.id.leftSticker);
                                    v.setBackground(ActivityUtil.getMainActivity().getResources().getDrawable(resourceId));
                                    v.setVisibility(View.VISIBLE);
                                    FragmentManager fg = getFragmentManager();
                                    RewardFragment fragment = (RewardFragment) fg.findFragmentById(R.id.fragmentVideoReward);
                                    fragment.getView().setVisibility(View.VISIBLE);


                                    //Store the sticker position

                                }
                            }
                        }
                    });


                    SparkPlug.start();
                    videoView.start();
                }


            }
        });

        mRecyclerView.addOnItemTouchListener(one);

        mAdapter.notifyDataSetChanged();

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





//
//       VideoInfo videoInfo = new VideoInfo();
//
//        String path = "android.resource://" + ActivityUtil.getMainActivity().getPackageName() + "/" + R.raw.bighero6clip;
//
//        Uri url = Uri.parse(path);
//
//        String displayName = "Big Hero 6 clip";//cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
//        String filePath = url.toString(); //cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
//
//        videoInfo.displayName = displayName;
//        videoInfo.filePath = path;
//
//
//        ContentResolver crThumb = getContentResolver();
//        BitmapFactory.Options options=new BitmapFactory.Options();
//        options.inSampleSize = 1;
//        Bitmap curThumb = null; //MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
//        System.out.println();
//        if(curThumb != null) {
//            videoInfo.thumbPath = String.valueOf(curThumb);
//        }
//
//
//        Cursor thumbCursor = managedQuery(
//                MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
//                thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
//                        + "=" + id, null, null);
//
//        if (thumbCursor.moveToFirst()) {
//            videoInfo.thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
//            System.out.println(videoInfo.thumbPath);
//        }
//
//
//        videos.add(videoInfo);







        cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.materialmenu, menu);
        return true;
    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(ActivityUtil.getMainActivity(), "Example if you want to add more", Toast.LENGTH_SHORT).show();
        }

        if (id == R.id.action_search) {
            MetricsStorage.getInstance().store();
            Toast.makeText(ActivityUtil.getMainActivity(), "Email Sent", Toast.LENGTH_SHORT).show();
        }

        if (id == R.id.action_restart) {


            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

         //   Toast.makeText(ActivityUtil.getMainActivity(), "Implement Server Call Here", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
















    private void initializeForTutorials() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<VideoInfo> videos = loadVideos();

        mAdapter = new MyAdapter(videos);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.removeOnItemTouchListener(one);
        mRecyclerView.removeOnItemTouchListener(two);


        two =  new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {


                if (cursor.moveToPosition(position)) {
                    int fileColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                    String videoFilePath = cursor.getString(fileColumn);

                    VideoView videoView = (VideoView) toggleVideoMode();
                    MediaController mediaController = new MediaController(ActivityUtil.getMainActivity());

                    videoView.setMediaController(mediaController);
                    videoView.setVideoPath(videoFilePath);


                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            toggleGalleryMode();
                            SparkPlug.stop();

                            View v = view.findViewById(R.id.rewardSticker);
                            v.setVisibility(View.VISIBLE);
                            FragmentManager fg = getFragmentManager();
                            RewardFragment fragment = (RewardFragment) fg.findFragmentById(R.id.fragmentVideoReward);
                            fragment.getView().setVisibility(View.VISIBLE);


                        }
                    });

                    videoView.start();
                }


            }
        });
        mRecyclerView.addOnItemTouchListener(two);


        mAdapter.notifyDataSetChanged();

    }










    Boolean check = true;

    public void orangeButtonPressed(View view) {
        Toast.makeText(ActivityUtil.getMainActivity(), "Orange Button Pressed", Toast.LENGTH_SHORT).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(check){
            toolbar.setTitle("Ergo Tutorials");
            initializeForTutorials();
        }else{
            toolbar.setTitle("Ergo");
            initialize();
        }
        check = !check;
    }

    public void continueVideos(View view) {
        FragmentManager fg = getFragmentManager();
        RewardFragment fragment = (RewardFragment) fg.findFragmentById(R.id.fragmentVideoReward);
        fragment.getView().setVisibility(View.INVISIBLE);
    }


}
