package uoftprojects.ergo.sensors.tilt;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import uoftprojects.ergo.metrics.Tilt;

/**
 * Created by Harsha Balasubramanian on 2/16/2015.
 */
public class TiltSensor implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float[] mGravity = null;
    private float tilt = 0;

    //private static TiltSensor INSTANCE = null;

    public TiltSensor(SensorManager sensorManager){
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        this.sensorManager = sensorManager;
    }

    /*public static TiltSensor getInstance(SensorManager sensorManager){
        if(INSTANCE == null){
            INSTANCE = new TiltSensor(sensorManager);
        }
        return INSTANCE;
    }*/

    public void unregister(){
        if(sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values.clone();

        if(mGravity != null){
            double norm_Of_g = Math.sqrt(mGravity[0] * mGravity[0] + mGravity[1] * mGravity[1] + mGravity[2] * mGravity[2]);

            mGravity[0] = (float) (mGravity[0] / norm_Of_g);
            mGravity[1] = (float) (mGravity[1] / norm_Of_g);
            mGravity[2] = (float) (mGravity[2] / norm_Of_g);

            tilt = (int) Math.round(Math.toDegrees(Math.acos(mGravity[2])));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public Tilt getTilt(){
        Tilt tilt = new Tilt(this.tilt);
        return tilt;
    }
}