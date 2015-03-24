package uoftprojects.ergo.util;

import android.content.SharedPreferences;

/**
 * Created by H on 3/9/2015.
 */
public final class StorageUtil {

    private static final SharedPreferences sharedPreferences = ActivityUtil.getCurrentActivity().getSharedPreferences("ErgoSetup", 0);
    private static final SharedPreferences.Editor editor = sharedPreferences.edit();

    public static boolean addLocalFlag(String flagName, boolean value){
        editor.putBoolean(flagName, value);
        return editor.commit();
    }

    public static boolean getFlag(String flagName){
        return sharedPreferences.getBoolean(flagName, false);
    }
}
