package uoftprojects.ergo.metrics;

/**
 * Created by H on 2/21/2015.
 */
public class Proximity implements IMetric {

    private boolean didDetectFace;

    public Proximity(boolean didDetectFace){
        this.didDetectFace = didDetectFace;
    }

    public boolean didDetectFace() {
        return this.didDetectFace;
    }

    @Override
    public MetricType getType() {
        return MetricType.Proximity;
    }
}