package uoftprojects.ergo.metrics;

/**
 * Created by H on 2/21/2015.
 */
public interface IMetric {

    public MetricType getType();

    public static enum MetricType{
        Tilt, Proximity, Time
    }
}
