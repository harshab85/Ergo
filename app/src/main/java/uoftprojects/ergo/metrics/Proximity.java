package uoftprojects.ergo.metrics;

/**
 * Created by Harsha Balasubramanian on 2/21/2015.
 */
public class Proximity implements IMetric {

    private boolean detectedFace;
    private long rectArea;
    private IMetric tilt;

    public Proximity(long rectArea, boolean detectedFace){
        this.rectArea = rectArea;
        this.detectedFace = detectedFace;
    }

    public IMetric getTilt() {
        return tilt;
    }

    public void setTilt(IMetric tilt) {
        this.tilt = tilt;
    }

    public boolean detectedFace() {
        return this.detectedFace;
    }

    public long getRectArea(){
        return this.rectArea;
    }

    @Override
    public MetricType getType() {
        return MetricType.Proximity;
    }
}
