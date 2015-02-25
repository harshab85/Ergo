package uoftprojects.ergo.metrics;

/**
 * Created by H on 2/21/2015.
 */
public class Proximity implements IMetric {

    private boolean didDetectFace;
    private long rectArea;

    public Proximity(boolean didDetectFace){
        this.didDetectFace = didDetectFace;
    }

    public Proximity(long rectArea, boolean didDetectFace){
        this.rectArea = rectArea;
        this.didDetectFace = didDetectFace;
    }

    public boolean didDetectFace() {
        return this.didDetectFace;
    }

    public long getRectArea(){
        return this.rectArea;
    }

    @Override
    public MetricType getType() {
        return MetricType.Proximity;
    }
}
