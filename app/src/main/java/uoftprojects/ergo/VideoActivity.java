package uoftprojects.ergo;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.MediaController;
import android.widget.SimpleAdapter;
import android.widget.VideoView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uoftprojects.ergo.engine.SparkPlug;
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


    private void initialize() {

        ActivityUtil.setMainActivity(this);

        List<VideoInfo> videos = loadVideos();
        List<Map<String, String>> aList = new ArrayList<>();
        for(int i=0 ; i<videos.size() ; i++){
            Map<String, String> hm = new HashMap<>();
            hm.put("thumbnail", videos.get(i).thumbPath);
            aList.add(hm);
        }

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new SimpleAdapter(this, aList, R.layout.video_layout, new String[]{"thumbnail"}, new int[]{R.id.thumbnail}));
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


                    View decorView = getWindow().getDecorView();
                    // Hide the status bar.
                    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);
                    // Remember that you should never show the action bar if the
                    // status bar is hidden, so hide that too if necessary.

                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            toggleGalleryMode();
                            SparkPlug.stop();

                        }
                    });
                    videoView.start();
                    SparkPlug.start();
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

    private View toggleGalleryMode() {
        VideoView videoView = (VideoView) findViewById(R.id.video_playback);
        videoView.setVisibility(View.INVISIBLE);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setVisibility(View.VISIBLE);

        return gridview;
    }

    private List<VideoInfo> loadVideos(){
        List<VideoInfo> videos = new ArrayList<>();

        String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID };

        String[] mediaColumns = { MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.MIME_TYPE };

        cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                VideoInfo videoInfo = new VideoInfo();

                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));

                Cursor thumbCursor = managedQuery(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                + "=" + id, null, null);

                if (thumbCursor.moveToFirst()) {
                    videoInfo.thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                }

                videoInfo.filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                videos.add(videoInfo);
            } while (cursor.moveToNext());
        }

        return videos;
    }
}

class VideoInfo {
    String filePath;
    String thumbPath;
}