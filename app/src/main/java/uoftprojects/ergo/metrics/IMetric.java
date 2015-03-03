package uoftprojects.ergo.metrics;

/**
 * Created by Harsha Balasubramanian on 2/21/2015.
 */
public interface IMetric {

    public MetricType getType();

    public static enum MetricType{
        Tilt, Proximity, Time
    }
}
