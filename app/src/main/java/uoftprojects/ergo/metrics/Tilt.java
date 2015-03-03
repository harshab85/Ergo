package uoftprojects.ergo.metrics;

/**
 * Created by Harsha Balasubramanian on 2/21/2015.
 */
public class Tilt implements IMetric {

    private float value;

    public Tilt(float value){
        this.value = value;
    }

    public float getValue() {
        return this.value;
    }

    @Override
    public MetricType getType() {
        return MetricType.Tilt;
    }
}
