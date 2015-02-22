package uoftprojects.ergo.metrics;

/**
 * Created by H on 2/21/2015.
 */
public class Tilt implements IMetric {

    private float value;
    private boolean didSensorChange;

    public Tilt(float value, boolean sensorChanged){
        this.value = value;
        this.didSensorChange = sensorChanged;
    }

    public boolean didSensorChange() {
        return this.didSensorChange;
    }

    public float getValue() {
        return this.value;
    }

    @Override
    public MetricType getType() {
        return MetricType.Tilt;
    }
}
