package uoftprojects.ergo.util;

import android.content.SharedPreferences;

/**
 * Created by H on 3/9/2015.
 */
public final class StorageUtil {

    private static final String STORAGE_NAME = "Ergo";
    private static final SharedPreferences sharedPreferences = ActivityUtil.getMainActivity().getSharedPreferences(STORAGE_NAME, 0);
    private static final SharedPreferences.Editor editor = sharedPreferences.edit();

    private enum UsageMetricKeys{
        CurrentTiltError("current_tilt_error"), AverageTiltError_Usage("avg_tilt_error_usage"),
        AverageTiltError_Day("avg_tilt_error_day"), TiltCorrection_AverageDelta("tilt_correction_delta"),

        CurrentProximityError("current_proximity_error"), AverageProximityError_Usage("avg_proximity_error_usage"),
        AverageProximityError_Day("avg_proximity_error_day"), ProximityCorrection_AverageDelta("proximity_correction_delta"),

        DeviceUsage_Minutes("device_usage_mins"), NumExercisesWatched("num_exercises_watched");
        private String key;

        UsageMetricKeys(String key) {
            this.key = key;
        }

        public String getKey(){
            return this.key;
        }
    }

    public static boolean addBoolean(String flagName, boolean value){
        editor.putBoolean(flagName, value);
        return editor.commit();
    }

    public static boolean getBoolean(String flagName){
        return sharedPreferences.getBoolean(flagName, false);
    }

    public static boolean addString(String key, String value){
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getString(String key){
        return sharedPreferences.getString(key, null);
    }

    public static boolean addLong(String key, long value){
        editor.putLong(key, value);
        return editor.commit();
    }

    public static long getLong(String key){
        return sharedPreferences.getLong(key, 0);
    }

    public static boolean addInt(String key, int value){
        editor.putInt(key, value);
        return editor.commit();
    }

    public static int getInt(String key){
        return sharedPreferences.getInt(key, -1);
    }

}
