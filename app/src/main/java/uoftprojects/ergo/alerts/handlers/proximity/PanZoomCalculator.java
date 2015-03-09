package uoftprojects.ergo.alerts.handlers.proximity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import uoftprojects.ergo.util.VideoUtil;

/**
 * Created by home on 2015-03-08.
 */
public class PanZoomCalculator {

    /// The current pan position
    PointF currentPan;
    /// The current zoom position
    float currentZoom;
    /// The windows dimensions that we are zooming/panning in
    View window;
    View child;
    Matrix matrix;
    // Pan jitter is a workaround to get the video view to update it's layout properly when zoom is changed
    int panJitter = 0;
    int anchor;

    PanZoomCalculator(View container, View child, int anchor) {
        // Initialize class fields
        currentPan = new PointF(0, 0);
        currentZoom = 1f;;
        this.window = container;
        this.child = child;
        matrix = new Matrix();
        this.anchor = anchor;
        onPanZoomChanged();
        this.child.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            // This catches when the image bitmap changes, for some reason it doesn't recurse

            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                onPanZoomChanged();
            }
        });
    }

    public void doZoom(float scale, PointF zoomCenter) {

        float oldZoom = currentZoom;

        // multiply in the zoom change
        currentZoom *= scale;

        // this limits the zoom
        currentZoom = Math.max(getMinimumZoom(), currentZoom);
        currentZoom = Math.min(8f, currentZoom);

        // Adjust the pan accordingly
        // Need to make it such that the point under the zoomCenter remains under the zoom center after the zoom

        // calculate in fractions of the image so:

        float width = window.getWidth();
        float height = window.getHeight();
        float oldScaledWidth = width * oldZoom;
        float oldScaledHeight = height * oldZoom;
        float newScaledWidth = width * currentZoom;
        float newScaledHeight = height * currentZoom;

        if (anchor == VideoUtil.VideoAnchor.CENTER.getValue()) {

            float reqXPos = ((oldScaledWidth - width) * 0.5f + zoomCenter.x - currentPan.x) / oldScaledWidth;
            float reqYPos = ((oldScaledHeight - height) * 0.5f + zoomCenter.y - currentPan.y) / oldScaledHeight;
            float actualXPos = ((newScaledWidth - width) * 0.5f + zoomCenter.x - currentPan.x) / newScaledWidth;
            float actualYPos = ((newScaledHeight - height) * 0.5f + zoomCenter.y - currentPan.y) / newScaledHeight;

            currentPan.x += (actualXPos - reqXPos) * newScaledWidth;
            currentPan.y += (actualYPos - reqYPos) * newScaledHeight;
        } else {
            // assuming top left
            float reqXPos = (zoomCenter.x - currentPan.x) / oldScaledWidth;
            float reqYPos = (zoomCenter.y - currentPan.y) / oldScaledHeight;
            float actualXPos = (zoomCenter.x - currentPan.x) / newScaledWidth;
            float actualYPos = (zoomCenter.y - currentPan.y) / newScaledHeight;
            currentPan.x += (actualXPos - reqXPos) * newScaledWidth;
            currentPan.y += (actualYPos - reqYPos) * newScaledHeight;
        }

        onPanZoomChanged();
    }

    public void doPan(float panX, float panY) {
        currentPan.x += panX;
        currentPan.y += panY;
        onPanZoomChanged();
    }

    private float getMinimumZoom() {
        return 1f;
    }

    /// Call this to reset the Pan/Zoom state machine
    public void reset() {
        // Reset zoom and pan
        currentZoom = getMinimumZoom();
        currentPan = new PointF(0f, 0f);
        onPanZoomChanged();
    }

    public void onPanZoomChanged() {

        // Things to try: use a scroll view and set the pan from the scrollview
        // when panning, and set the pan of the scroll view when zooming

        float winWidth = window.getWidth();
        float winHeight = window.getHeight();

        if (currentZoom <= 1f) {
            currentPan.x = 0;
            currentPan.y = 0;
        } else if (anchor == VideoUtil.VideoAnchor.CENTER.getValue()) {

            float maxPanX = (currentZoom - 1f) * window.getWidth() * 0.5f;
            float maxPanY = (currentZoom - 1f) * window.getHeight() * 0.5f;
            currentPan.x = Math.max(-maxPanX, Math.min(maxPanX, currentPan.x));
            currentPan.y = Math.max(-maxPanY, Math.min(maxPanY, currentPan.y));
        } else {
            // assume top left

            float maxPanX = (currentZoom - 1f) * window.getWidth();
            float maxPanY = (currentZoom - 1f) * window.getHeight();
            currentPan.x = Math.max(-maxPanX, Math.min(0, currentPan.x));
            currentPan.y = Math.max(-maxPanY, Math.min(0, currentPan.y));
        }

        if (child instanceof ImageView && ((ImageView) child).getScaleType()== ImageView.ScaleType.MATRIX) {
            ImageView view = (ImageView) child;
            Drawable drawable = view.getDrawable();
            if (drawable != null) {
                Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
                if (bm != null) {
                    // Limit Pan

                    float bmWidth = bm.getWidth();
                    float bmHeight = bm.getHeight();

                    float fitToWindow = Math.min(winWidth / bmWidth, winHeight / bmHeight);
                    float xOffset = (winWidth - bmWidth * fitToWindow) * 0.5f * currentZoom;
                    float yOffset = (winHeight - bmHeight * fitToWindow) * 0.5f * currentZoom;

                    matrix.reset();
                    matrix.postScale(currentZoom * fitToWindow, currentZoom * fitToWindow);
                    matrix.postTranslate(currentPan.x + xOffset, currentPan.y + yOffset);
                    ((ImageView) child).setImageMatrix(matrix);
                }
            }
        } else {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();

            lp.leftMargin = (int) currentPan.x + panJitter;
            lp.topMargin = (int) currentPan.y;
            lp.width = (int) (window.getWidth() * currentZoom);
            lp.height = (int) (window.getHeight() * currentZoom);
            panJitter ^= 1;

            child.setLayoutParams(lp);
        }
    }
}
