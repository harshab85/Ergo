package uoftprojects.ergo.util;

import android.app.Activity;

/**
 * Created by Harsha Balasubramanian on 3/3/2015.
 */
public final class ActivityUtil {

    private static Activity currentActivity;

    public static void setCurrentActivity(Activity activity){
        currentActivity = activity;
    }

    public static Activity getCurrentActivity(){
        return currentActivity;
    }
}
