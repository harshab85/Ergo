package uoftprojects.ergo.sensors.proximity;

import android.graphics.Rect;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.widget.Toast;

import uoftprojects.ergo.metrics.Proximity;
import uoftprojects.ergo.util.ActivityUtil;

/**
 * Created by Harsha Balasubramanian on 2/22/2015.
 */
public class ProximitySensor implements Camera.FaceDetectionListener {

    private Camera camera;
    private boolean detectedFace;
    private long rectArea;

    //private static ProximitySensor INSTANCE = null;

    public ProximitySensor(){
        register();
    }

    /*public static ProximitySensor getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ProximitySensor();
        }
        return INSTANCE;
    }*/

    private void register(){
        try {
            int id = getFrontFacingCameraId();

            if(this.camera != null){
                this.camera.release();
            }

            this.camera = Camera.open(id);
            initialize(new DummySurfaceHolder());
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private int getFrontFacingCameraId() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int i = 0;
        for (; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                break;
            }
        }
        return i;
    }

    private void initialize(SurfaceHolder holder) {

        try {
            camera.stopPreview();
        }
        catch (Throwable swallow) {
            // ignore: tried to stop a non-existent preview
        }

        try {
            int rotation = ActivityUtil.getMainActivity().getWindowManager().getDefaultDisplay().getRotation();
            switch (rotation){
                case 0:
                    camera.setDisplayOrientation(90);
                    break;
                case 1:
                    camera.setDisplayOrientation(0);
                    break;
                case 3:
                    camera.setDisplayOrientation(180);
                    break;
            }

            camera.setPreviewDisplay(holder);
            camera.startPreview();
            camera.setFaceDetectionListener(this);
            camera.startFaceDetection();
        }
        catch (Throwable e) {
            Toast.makeText(ActivityUtil.getMainActivity(), "Error in proximity sensor", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {

        if (faces.length > 0) {
            this.detectedFace = true;
            Camera.Face face = faces[0];
            Rect rect = face.rect;
            int width = Math.abs(rect.width());
            int height = Math.abs(rect.height());
            this.rectArea = width * height;
        }
        else{
            this.detectedFace = false;
            this.rectArea = 0;
        }
    }

    public Proximity getProximity(){
        Proximity proximity = new Proximity(this.rectArea, this.detectedFace);
        return proximity;
    }

    public void unregister(){
        if (camera != null) {
            camera.release();
        }
    }
}
