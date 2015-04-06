package uoftprojects.ergo.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by H on 3/9/2015.
 */
public final class SetupUtil {

    private static final String SETUP_COMPLETED_FLAG_NAME = "setupCompleted";

    private static final String WATCHED_FIRST_VIDEO = "watchedFirstVideo";

    public static final String APP_START_DATE = "appStartDate";


    public static boolean isSetupCompeted() {
        return StorageUtil.getBoolean(SETUP_COMPLETED_FLAG_NAME);
    }

    public static void setupCompleted() {
        StorageUtil.addBoolean(SETUP_COMPLETED_FLAG_NAME, true);
    }

    public static boolean hasWatchedFirstVideo(){
        return StorageUtil.getBoolean(WATCHED_FIRST_VIDEO);
    }

    public static void watchedFirstVideo(){
        StorageUtil.addBoolean(WATCHED_FIRST_VIDEO, true);
    }

    public static void setAppStartDate(){
        long currDate = Calendar.getInstance().getTimeInMillis();
        StorageUtil.addLong(APP_START_DATE, currDate);
    }

    public static long getAppStartDate(){
        return StorageUtil.getLong(APP_START_DATE);
    }


}
