package uoftprojects.ergo.metrics.usage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import uoftprojects.ergo.email.EmailClient;
import uoftprojects.ergo.util.SetupUtil;
import uoftprojects.ergo.util.StorageUtil;

/**
 * Created by harsha on 2015-04-02.
 */
public final class MetricsStorage {

    /*
        Singleton
     */
    private static final MetricsStorage INSTANCE = new MetricsStorage();

    public static final String METRICS_STORAGE_KEY = "metrics";

    private MetricsStorage(){
    }

    public static MetricsStorage getInstance(){
        return INSTANCE;
    }

    public void initialize(String metrics){
        try {
            JSONObject storedMetrics = new JSONObject(metrics);
            JSONObject activeMetrics = storedMetrics.getJSONObject("activeMetrics");
            this.currProximityErrors = activeMetrics.getInt("proximityErrors");
            this.currTiltErrors = activeMetrics.getInt("tiltErrors");
            this.currVideosWatched = activeMetrics.getInt("videosWatched");
            this.currVideosWatched_Minutes = activeMetrics.getInt("minutesWatched");
        }
        catch (JSONException e) {
            //Ignore
        }
    }


    // Number of tilt errors in the app's current usage
    private int currTiltErrors;

    // Number of proximity errors in the app's current usage
    private int currProximityErrors;

    // Number of videos watched in the app's current usage
    private int currVideosWatched;

    // Number of minutes videos were watched in the current usage
    private long currVideosWatched_Minutes;


    /*
        Update apis
     */
    public void updateCurrTiltErrors() {
        this.currTiltErrors++;
    }

    public void updateCurrProximityErrors() {
        this.currProximityErrors++;
    }

    public void updateCurrVideoWatched_Minutes(long minutes) {
        this.currVideosWatched++;
        this.currVideosWatched_Minutes += minutes;
    }

    public int getCurrTiltErrors() {
        return currTiltErrors;
    }

    public int getCurrProximityErrors() {
        return currProximityErrors;
    }

    // storage
    public void store(){
        // Get previous values from shared pref
        String metrics = StorageUtil.getString(METRICS_STORAGE_KEY);

        // if not present (first-time), store current values directly
        if(metrics == null || metrics.isEmpty()){
            metrics = createInitialMetrics();

        }
        // If found, update json object with new values
        else{
            metrics = updateMetrics(metrics);
        }

        StorageUtil.addString(METRICS_STORAGE_KEY, metrics);
    }

    private String updateMetrics(String metrics) {

        try {
            JSONObject storedMetrics = new JSONObject(metrics);

            JSONObject activeMetrics = storedMetrics.getJSONObject("activeMetrics");
            long endDate = activeMetrics.getLong("endDate");
            long currDate = Calendar.getInstance().getTimeInMillis();

            if(currDate >= endDate){
                if(storedMetrics.has("historicalMetrics")){
                    JSONArray historicalMetrics = storedMetrics.getJSONArray("historicalMetrics");
                    historicalMetrics.put(activeMetrics);
                }
                else{
                    JSONArray historicalMetrics = new JSONArray();
                    historicalMetrics.put(activeMetrics);
                    storedMetrics.put("historicalMetrics", historicalMetrics);
                }

                clearMetrics();
                JSONObject currentMetrics = getCurrentMetrics();
                int currentWeek = activeMetrics.getInt("week") + 1;
                currentMetrics.put("week", currentWeek);
                storedMetrics.put("activeMetrics", currentMetrics);

                // Send email
                //String emailAddress = StorageUtil.getString("emailAddress");
                //.getInstance().send(emailAddress, activeMetrics);
            }
            else{
                JSONObject currentMetrics = getCurrentMetrics();
                storedMetrics.put("activeMetrics", currentMetrics);
            }

            String emailAddress = StorageUtil.getString("emailAddress");
            if(emailAddress != null && !emailAddress.isEmpty()) {
                EmailClient.getInstance().send(emailAddress, activeMetrics);
            }

            return storedMetrics.toString();
        }
        catch (Exception e){
            // Ignore
        }

        return metrics;
    }

    private void clearMetrics() {
        this.currVideosWatched = 0;
        this.currTiltErrors = 0;
        this.currVideosWatched_Minutes = 0;
        this.currProximityErrors = 0;
    }

    public MetricsComparison compare(JSONObject one, JSONObject two){

        try {

            if(one == null){
                return new MetricsComparison(two.getInt("tiltErrors"), two.getInt("proximityErrors"));
            }

            int tiltErrors1 = one.getJSONObject("activeMetrics").getInt("tiltErrors");
            int tiltErrors2 = two.getInt("tiltErrors");
            int tiltDelta = tiltErrors2 - tiltErrors1;

            int proximityErrors1 = one.getJSONObject("activeMetrics").getInt("proximityErrors");
            int proximityErrors2 = two.getInt("proximityErrors");
            int proximityDelta = proximityErrors2 - proximityErrors1;

            return new MetricsComparison(tiltDelta, proximityDelta);

        }
        catch (Exception e){
            // Ignore
        }

        return null;

    }

    private String createInitialMetrics() {

        JSONObject initialMetrics = new JSONObject();

        long appStartDate = StorageUtil.getLong(SetupUtil.APP_START_DATE);

        try {
            initialMetrics.put("appStartDate", appStartDate);

            JSONArray metricsArray = new JSONArray();
            initialMetrics.put("historicalMetrics", metricsArray);

            JSONObject activeMetrics = getCurrentMetrics();

            initialMetrics.put("activeMetrics", activeMetrics);

        }
        catch (Exception e){
            // Ignore
        }

        return initialMetrics.toString();
    }

    public JSONObject getCurrentMetrics() throws JSONException {
        JSONObject currentMetrics = new JSONObject();
        currentMetrics.put("tiltErrors", currTiltErrors);
        currentMetrics.put("proximityErrors", currProximityErrors);
        currentMetrics.put("videosWatched", currVideosWatched);
        currentMetrics.put("minutesWatched", currVideosWatched_Minutes);

        Calendar calendar = Calendar.getInstance();
        long currTime = calendar.getTimeInMillis();
        currentMetrics.put("updatedOn", currTime);
        currentMetrics.put("startDate", currTime);

        calendar.add(Calendar.DAY_OF_MONTH, 7);
        long nextWeek = calendar.getTimeInMillis();
        currentMetrics.put("endDate", nextWeek);

        currentMetrics.put("week", 1);

        return currentMetrics;
    }


    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder();
        sb.append("TiltErrors:");
        sb.append(currTiltErrors);
        sb.append(",ProximityErrors:");
        sb.append(currProximityErrors);
        sb.append(",videosWatched:");
        sb.append(currVideosWatched);
        sb.append(" Watched: ");
        sb.append(currVideosWatched_Minutes);

        return super.toString();
    }
}
