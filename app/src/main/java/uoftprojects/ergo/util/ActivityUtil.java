package uoftprojects.ergo.util;

import android.app.Activity;

/**
 * Created by Harsha Balasubramanian on 3/3/2015.
 */
public final class ActivityUtil {

    private static Activity mainActivity;

    public static void setMainActivity(Activity activity){
        mainActivity = activity;
    }

    public static Activity getMainActivity(){
        return mainActivity;
    }
}
