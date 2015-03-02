package uoftprojects.ergo;

import android.app.Activity;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import uoftprojects.ergo.engine.AlertsEngine;
import uoftprojects.ergo.engine.ErgoEngine;
import uoftprojects.ergo.metrics.IMetric;


public class VideoActivity extends Activity {

    private Cursor cursor;

    // Flag used to ensure that ergo engine and alert engine are created only once during the app's lifetime
    private static boolean createOnce = true;

    private static ErgoEngine ergoEngine = null;

    private static AlertsEngine alertsEngine = null;

    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

            List<VideoInfo> videos = loadVideos();

            final Activity activity = this;

            GridView gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new VideoGalleryAdapter(this, videos));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (cursor.moveToPosition(position)) {
                        int fileColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                        String videoFilePath = cursor.getString(fileColumn);

                        VideoView videoView = (VideoView) toggleVideoMode();
                        MediaController mediaController = new MediaController(activity);

                        videoView.setMediaController(mediaController);
                        videoView.setVideoPath(videoFilePath);

                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                toggleGalleryMode();
                            }
                        });

                        videoView.start();
                    }
                }
            });

        if(createOnce) {
            ergoEngine = new ErgoEngine(this);
            alertsEngine = new AlertsEngine(this);

            createMetricsUpdateLoop();

            createOnce = false;
        }
    }

    private View toggleVideoMode(){
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setVisibility(View.INVISIBLE);

        VideoView videoView = (VideoView) findViewById(R.id.video_playback);
        videoView.setVisibility(View.VISIBLE);

        return videoView;
    }

    private View toggleGalleryMode(){
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
                videoInfo.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                videoInfo.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                videos.add(videoInfo);
            } while (cursor.moveToNext());
        }

        return videos;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ergoEngine.register();
    }

    @Override
    protected void onPause() {
        ergoEngine.unregister();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        ergoEngine.unregister();
        super.onDestroy();
    }

    /*
            Run a timer every 5 seconds to get metric updates
         */
    private void createMetricsUpdateLoop(){

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                GetUpdates getUpdates = new GetUpdates();
                getUpdates.execute();
            }
        }, 5000, 3000);
    }


    class GetUpdates extends AsyncTask<String, Void, List<IMetric>> {

        @Override
        protected List<IMetric> doInBackground(String... params) {
            final IMetric tilt = ergoEngine.getTilt();
            final IMetric proximity = ergoEngine.getProximity();
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView currentPhoneTilt = (TextView)findViewById(R.id.current_PhoneTilt);
                    currentPhoneTilt.setText("Current phone angle is " + ((Tilt)tilt).getValue());

                    TextView currProximity = (TextView)findViewById(R.id.curr_proximity);
                    currProximity.setText("Current rect area angle is " + ((Proximity)proximity).getRectArea());
                }
            });*/



            IMetric startTime = ergoEngine.getStartTime();

            List<IMetric> metricsList = new ArrayList<>();
            metricsList.add(tilt);
            metricsList.add(proximity);
            metricsList.add(startTime);
            return metricsList;
        }

        @Override
        protected void onPostExecute(List<IMetric> metricList) {
            alertsEngine.testWithBaseLine(metricList);
        }


    }
}

class VideoInfo {
    String filePath;
    String mimeType;
    String thumbPath;
    String title;
}

class VideoGalleryAdapter extends BaseAdapter {
    private Activity activity;
    private List<VideoInfo> videoItems;

    public VideoGalleryAdapter(Activity activity, List<VideoInfo> videoItems) {
        this.activity = activity;
        this.videoItems = videoItems;
    }

    public int getCount() {
        return videoItems.size();
    }

    public Object getItem(int position) {
        return videoItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(activity);
            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageURI(Uri.parse(videoItems.get(position).thumbPath));
        return imageView;
    }
}