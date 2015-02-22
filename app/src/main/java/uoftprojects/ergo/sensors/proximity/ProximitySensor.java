package uoftprojects.ergo.sensors.proximity;

import android.app.Activity;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import uoftprojects.ergo.metrics.Proximity;

/**
 * Created by H on 2/22/2015.
 */
public class ProximitySensor implements Camera.FaceDetectionListener {

    private Activity activity;
    private Camera camera;
    private boolean detectedFace;

    private static ProximitySensor INSTANCE = null;

    private ProximitySensor(Activity activity){
        this.activity = activity;
        register();
    }

    public static ProximitySensor getInstance(Activity activity){
        if(INSTANCE == null){
            INSTANCE = new ProximitySensor(activity);
        }
        return INSTANCE;
    }

    private void register(){
        try {
            int id = getFrontFacingCameraId();

            if(this.camera != null){
                this.camera.release();
            }

            this.camera = Camera.open(id);
            initialise(new DummySurfaceHolder(), activity);
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

    private void initialise(SurfaceHolder holder, Activity activity) {

        try {
            camera.stopPreview();
        }
        catch (Throwable swallow) {
            // ignore: tried to stop a non-existent preview
        }

        try {
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
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
            e.printStackTrace();
        }
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {

        if (faces.length > 0) {
            this.detectedFace = true;
        }

        // TODO Improve this using rect
    }

    public Proximity getProximity(){
        boolean val = this.detectedFace;
        Proximity proximity = new Proximity(val);
        this.detectedFace = false;
        return proximity;
    }

    public void unregister(){
        if (camera != null) {
            camera.release();
        }
    }
}
