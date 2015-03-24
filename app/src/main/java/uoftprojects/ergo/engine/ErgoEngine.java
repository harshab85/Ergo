package uoftprojects.ergo.engine;

import android.app.Activity;
import android.hardware.SensorManager;

import uoftprojects.ergo.metrics.IMetric;
import uoftprojects.ergo.sensors.proximity.ProximitySensor;
import uoftprojects.ergo.sensors.tilt.TiltSensor;
import uoftprojects.ergo.sensors.timer.Timer;
import uoftprojects.ergo.util.ActivityUtil;

/**
 * Created by Harsha Balasubramanian on 2/16/2015.
 */
public class ErgoEngine implements IRegistration {

    private TiltSensor tiltSensor = null;

    private ProximitySensor proximitySensor = null;

    private Timer timer = null;

    public ErgoEngine(){
        register();
    }

    @Override
    public void register() {
        SensorManager sensorManager = (SensorManager) ActivityUtil.getCurrentActivity().getSystemService(Activity.SENSOR_SERVICE);

        tiltSensor = new TiltSensor(sensorManager);

        timer = Timer.getInstance();

        proximitySensor = new ProximitySensor();//.getInstance();
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
