package uoftprojects.ergo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

        ActivityUtil.setMainActivity(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ergo Video Gallery");

        if(!SetupUtil.isSetupCompeted()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String storedMetrics = StorageUtil.getString(MetricsStorage.METRICS_STORAGE_KEY);
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

        List<VideoInfo> videos = loadVideos();

        mAdapter = new MyAdapter(videos);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        if (cursor.moveToPosition(position)) {
                            int fileColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                            String videoFilePath = cursor.getString(fileColumn);


                            Intent intent = new Intent(ActivityUtil.getMainActivity(), VideoActivity.class);
                            intent.putExtra("videoFilePath", videoFilePath);
                            ActivityUtil.getMainActivity().startActivity(intent);
                            ActivityUtil.getMainActivity().finish();
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
        MetricsStorage.getInstance().store();
        RewardsList.getInstance().store();
        super.onPause();
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

    class VideoInfo{
        String thumbPath;
        String displayName;
        String filePath;
    }

}
