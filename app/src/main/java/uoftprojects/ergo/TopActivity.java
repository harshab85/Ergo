package uoftprojects.ergo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;

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
import uoftprojects.ergo.util.ActivityUtil;
import android.content.SharedPreferences;


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
        setActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);



        // CrimeListFragment.java - onCreateView()
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

        ActivityUtil.setMainActivity(this);

        videoView.setVisibility(View.INVISIBLE);

        toolbar.setTitle("Ergo Video Player!");

        SharedPreferences sharedPreferences = getSharedPreferences("ErgoSetup", 0);

        if(sharedPreferences != null) {
            boolean setupCompleted = sharedPreferences.getBoolean("setupCompleted", false);
            if (!setupCompleted) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }




        initialize();

//
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.add(RewardFragment.newInstance("hi","no"), null);
//        ft.commit();






//
//
//        // Create new fragment and transaction
//        Fragment newFragment = new RewardFragment();
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//// Replace whatever is in the fragment_container view with this fragment,
//// and add the transaction to the back stack
//        transaction.replace(R.id.rewardLocation, newFragment);
//        transaction.addToBackStack(null);
//
//// Commit the transaction
//        transaction.commit();
//










      //  startActivity(new Intent(this, RewardFragment.class));


//        RewardFragment nextFrag= new RewardFragment();
//        this.getFragmentManager().beginTransaction()
//                .replace(R.id.rewards, nextFrag, null)
//                .addToBackStack(null)
//                .commit();

    }

    private void initialize() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)

//        String[] mDataset = new String[10];
//
//        for(int i = 0 ; i < mDataset.length; i++){
//            mDataset[i] = "Title:"+i;
//        }


        List<VideoInfo> videos = loadVideos();

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

                            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    toggleGalleryMode();
                                    SparkPlug.stop();
                                    Toast.makeText(ActivityUtil.getMainActivity(), "SUCCESS!", Toast.LENGTH_SHORT).show();


//                                    RewardFragment nextFrag= new RewardFragment();
//                                    ActivityUtil.getMainActivity().getFragmentManager().beginTransaction()
//                                            .replace(R.id.rewards, nextFrag, null)
//                                            .addToBackStack(null)
//                                            .commit();
                                    //Check for reward

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




    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

    @Override
    protected void onPause() {
        SparkPlug.stop();
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






       VideoInfo videoInfo = new VideoInfo();

        String path = "android.resource://" + ActivityUtil.getMainActivity().getPackageName() + "/" + R.raw.bighero6clip;

        Uri url = Uri.parse(path);

        String displayName = "Big Hero 6 clip";//cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
        String filePath = url.toString(); //cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

        videoInfo.displayName = displayName;
        videoInfo.filePath = path;


        ContentResolver crThumb = getContentResolver();
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap curThumb = null; //MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
        System.out.println();
        if(curThumb != null) {
            videoInfo.thumbPath = String.valueOf(curThumb);
        }


//        Cursor thumbCursor = managedQuery(
//                MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
//                thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
//                        + "=" + id, null, null);

//        if (thumbCursor.moveToFirst()) {
//            videoInfo.thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
//            System.out.println(videoInfo.thumbPath);
//        }


        videos.add(videoInfo);







//        cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                mediaColumns, null, null, null);

//

//
//        cursor = managedQuery(url,mediaColumns, null, null, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                VideoInfo videoInfo = new VideoInfo();
//
//                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
//
//                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
//                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
//
//                videoInfo.displayName = displayName;
//                videoInfo.filePath = filePath;
//
//
//                ContentResolver crThumb = getContentResolver();
//                BitmapFactory.Options options=new BitmapFactory.Options();
//                options.inSampleSize = 1;
//                Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
//                System.out.println();
//                if(curThumb != null) {
//                    videoInfo.thumbPath = String.valueOf(curThumb);
//                }
//
//
//                Cursor thumbCursor = managedQuery(
//                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
//                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
//                                + "=" + id, null, null);
//
//                if (thumbCursor.moveToFirst()) {
//                    videoInfo.thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
//                    System.out.println(videoInfo.thumbPath);
//                }
//
//
//                videos.add(videoInfo);
//
//
//            } while (cursor.moveToNext());
//        }


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
            Toast.makeText(ActivityUtil.getMainActivity(), "Implement Server Call Here", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }



    public void orangeButtonPressed(View view) {
        //noinspection SimplifiableIfStatement

        Toast.makeText(ActivityUtil.getMainActivity(), "Orange Button Pressed", Toast.LENGTH_SHORT).show();
    }

}
