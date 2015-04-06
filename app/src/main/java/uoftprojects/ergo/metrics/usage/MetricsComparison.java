package uoftprojects.ergo.metrics.usage;

/**
 * Created by harsha on 2015-04-04.
 */
public class MetricsComparison{

        private int tiltDelta;
        private int proximityDelta;

        public int getTiltDelta() {
            return tiltDelta;
        }

        public int getProximityDelta() {
            return proximityDelta;
        }

        MetricsComparison(int tiltDelta, int proximityDelta){
            this.tiltDelta = tiltDelta;
            this.proximityDelta = proximityDelta;
        }
    }