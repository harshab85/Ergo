package uoftprojects.ergo.engine;

import android.app.Activity;
import android.hardware.SensorManager;

import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.sensors.proximity.ProximitySensor;
import uoftprojects.ergo.sensors.tilt.TiltSensor;
import uoftprojects.ergo.sensors.timer.Timer;

/**
 * Created by H on 2/16/2015.
 */
public class ErgoEngine implements IRegistration {

    private Activity parentActivity;

    private TiltSensor tiltSensor = null;

    private ProximitySensor proximitySensor = null;

    private Timer timer = null;

    public ErgoEngine(Activity parentActivity){
        this.parentActivity = parentActivity;
        register();
    }

    @Override
    public void register() {
        SensorManager sensorManager = (SensorManager)this.parentActivity.getSystemService(Activity.SENSOR_SERVICE);
        tiltSensor = TiltSensor.getInstance(sensorManager);

        timer = Timer.getInstance();

        proximitySensor = ProximitySensor.getInstance(this.parentActivity);
    }

    @Override
    public void unregister() {
        tiltSensor.unregister();
        proximitySensor.unregister();
    }

    public IMetric getTilt(){
        return tiltSensor.getTilt();
    }

    public IMetric getProximity(){
        return proximitySensor.getProximity();
    }

    public IMetric getStartTime(){
        return timer.getStartTime();
    }
}
