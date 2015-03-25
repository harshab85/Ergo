package uoftprojects.ergo.alerts.handlers.baseline;

/**
 * Created by Harsha Balasubramanian on 2/24/2015.
 */
public final class Baseline {

    // Tilt Baseline
    public static final int MIN_TILT_ANGLE = 40;
    public static final int MAX_TILT_ANGLE = 70;
    public static final int PHONE_FLAT_MAX_ANGLE = 10;
    public static final int PHONE_MIN_USAGE_ANGLE = 20;
    public static final long[] VIBRATION_ALERT_PATTERN = new long[]{1000, 50};

    // Timer Baseline
    public static final long MAX_CONTINUOUS_DEVICE_TIME = 60000;

    // Proximity Baseline
    public static final long MIN_RECT_AREA = 500000;
    public static final long MAX_RECT_AREA = 800000;

    // Sensor polling - msec
    public static final int POLL_START_DELAY = 1000;
    public static final int POLL_INTERVAL = 1000;



    // Max zoom factor
    public static final double MAX_ZOOM_FACTOR = 2.0;
}
