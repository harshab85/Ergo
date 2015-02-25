package uoftprojects.ergo.metrics;

/**
 * Created by H on 2/22/2015.
 */
public class StartTime implements IMetric {

    private long startTime;

    public StartTime(long startTime){
        this.startTime = startTime;
    }

    public long getTime(){
        return this.startTime;
    }

    @Override
    public MetricType getType() {
        return MetricType.Time;
    }
}
