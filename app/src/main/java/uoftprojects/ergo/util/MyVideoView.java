package uoftprojects.ergo.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by ryanprimeau on 15-03-24.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class MyVideoView extends VideoView {

    private int mVideoWidth = 100;
    private int mVideoHeight = 100;

    private float scale = 1;
    private DisplayMode displayMode = DisplayMode.SCALE;

    public enum DisplayMode {
        ORIGINAL,       // original aspect ratio
        FULL_SCREEN,    // fit to screen
        ZOOM,            // zoom in
        SCALE
    };


    public MyVideoView(Context context) {
        super(context);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mVideoWidth = 100;
        mVideoHeight = 100;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        int width2 = width;
        int height2 = height;

        if (displayMode == DisplayMode.ORIGINAL) {
            if (mVideoWidth > 0 && mVideoHeight > 0) {
                if ( mVideoWidth * height  > width * mVideoHeight ) {
                    // video height exceeds screen, shrink it
                    height = width * mVideoHeight / mVideoWidth;
                } else if ( mVideoWidth * height  < width * mVideoHeight ) {
                    // video width exceeds screen, shrink it
                    width = height * mVideoWidth / mVideoHeight;
                } else {
                    // aspect ratio is correct
                }
            }
        }
        else if (displayMode == DisplayMode.FULL_SCREEN) {
            // just use the default screen width and screen height
        }
        else if (displayMode == DisplayMode.ZOOM) {
            // zoom video
            if (mVideoWidth > 0 && mVideoHeight > 0 && mVideoWidth < width) {
                height = mVideoHeight * width / mVideoWidth;
            }
        }

        else if (displayMode == DisplayMode.SCALE) {


            System.out.println("HERE1");
            width2 = (int)(((float)width)*scale);
            height2 = (int)(((float)height)*scale);

            System.out.println("HERE");
            System.out.println(width);
            System.out.println(height);
        }

        setMeasuredDimension(width2, height2);
    }

    public void changeVideoSize(float scale2)
    {
//        mVideoWidth = (int) (mVideoWidth*scale);
//        mVideoHeight = (int) (mVideoHeight*scale);

        scale = scale2;
        // not sure whether it is useful or not but safe to do so
   //     getHolder().setFixedSize(mVideoWidth, mVideoHeight);

        requestLayout();
        invalidate();     // very important, so that onMeasure will be triggered
    }

    public void setDisplayMode(DisplayMode mode) {
        displayMode = mode;
    }
}