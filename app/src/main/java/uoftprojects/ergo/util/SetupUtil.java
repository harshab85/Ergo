package uoftprojects.ergo.util;

/**
 * Created by H on 3/9/2015.
 */
public final class SetupUtil {

    public static final String SETUP_COMPLETED_FLAG_NAME = "setupCompleted";

    public static boolean isSetupCompeted() {
        return StorageUtil.getFlag(SETUP_COMPLETED_FLAG_NAME);
    }

    public static void setupCompleted() {
        StorageUtil.addLocalFlag(SETUP_COMPLETED_FLAG_NAME, true);
    }

}
