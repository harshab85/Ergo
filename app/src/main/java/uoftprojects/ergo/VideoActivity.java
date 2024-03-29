package uoftprojects.ergo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.MediaController;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.VideoView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uoftprojects.ergo.alerts.handlers.AlertsHandler;
import uoftprojects.ergo.engine.SparkPlug;
import uoftprojects.ergo.metrics.usage.MetricsStorage;
import uoftprojects.ergo.util.ActivityUtil;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public class VideoActivity extends Activity {

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initialize();
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
        MetricsStorage.getInstance().toString();
    }

    private void initialize() {

        ActivityUtil.setMainActivity(this);

        List<VideoInfo> videos = loadVideos();

        if(videos == null || videos.isEmpty()){
            Toast.makeText(this, "No videos in phone library.", Toast.LENGTH_SHORT).show();
        }/*else{
            Toast.makeText(this, "Yes videos!", Toast.LENGTH_SHORT).show();
        }*/

        //System.out.println("NO ERROR YET?");

        List<Map<String, String>> aList = new ArrayList<>();
        for(int i=0 ; i<videos.size() ; i++){
            Map<String, String> hm = new HashMap<>();
            hm.put("thumbnail", videos.get(i).thumbPath);
            hm.put("name", videos.get(i).displayName);
            aList.add(hm);
        }

        GridView gridview = (GridView) findViewById(R.id.gridview);

        String[] rows = new String[]{"thumbnail", "name"};
        int[] ids = new int[]{R.id.thumbnail, R.id.thumbnail_name};

        gridview.setAdapter(new SimpleAdapter(this, aList, R.layout.video_layout, rows, ids));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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


                            long minutes = 0;
                            long duration_msec = mp.getDuration();
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
                    });

                    SparkPlug.start();
                    videoView.start();
                }
            }
        });
    }

    private View toggleVideoMode() {
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setVisibility(View.INVISIBLE);

        VideoView videoView = (VideoView) findViewById(R.id.video_playback);
        videoView.setVisibility(View.VISIBLE);

        return videoView;
    }

    private View  toggleGalleryMode() {
        VideoView videoView = (VideoView) findViewById(R.id.video_playback);
        videoView.setVisibility(View.INVISIBLE);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setVisibility(View.VISIBLE);

        return gridview;
    }

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


                /*ContentResolver crThumb = getContentResolver();
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inSampleSize = 1;
                Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
                System.out.println();
                if(curThumb != null) {
                    videoInfo.thumbPath = curThumb;
                }*/


                Cursor thumbCursor = managedQuery(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                + "=" + id, null, null);

                if (thumbCursor.moveToFirst()) {
                    videoInfo.thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                    //System.out.println(videoInfo.thumbPath);
                }


                videos.add(videoInfo);
            } while (cursor.moveToNext());
        }

        return videos;
    }
}

class VideoInfo {
    String displayName;
    String filePath;
    String thumbPath;
}
