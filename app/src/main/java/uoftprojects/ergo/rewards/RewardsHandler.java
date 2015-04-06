package uoftprojects.ergo.rewards;

import android.media.MediaPlayer;

import org.json.JSONException;
import org.json.JSONObject;


import uoftprojects.ergo.R;
import uoftprojects.ergo.metrics.usage.MetricsComparison;
import uoftprojects.ergo.metrics.usage.MetricsStorage;
import uoftprojects.ergo.util.ActivityUtil;
import uoftprojects.ergo.util.BaselineUtil;
import uoftprojects.ergo.util.SetupUtil;

/**
 * Created by harsha on 2015-04-04.
 */
public final class RewardsHandler {

    private JSONObject initialMetrics;

    public RewardsHandler(JSONObject initialMetrics){
        this.initialMetrics = initialMetrics;
    }

    public boolean shouldUnlockReward(){
        if(!SetupUtil.hasWatchedFirstVideo()){

            // Play first sticker video
            //MediaPlayer mediaPlayer = MediaPlayer.create(ActivityUtil.getMainActivity(), R.raw.ergo_first_sticker);
            //mediaPlayer.start();

            //SetupUtil.watchedFirstVideo();
            return true;
        }
        else{
            try {
                JSONObject currentMetrics = MetricsStorage.getInstance().getCurrentMetrics();
                MetricsComparison metricsComparison = MetricsStorage.getInstance().compare(initialMetrics, currentMetrics);
                if(metricsComparison.getProximityDelta() > BaselineUtil.MAX_PROXIMITY_ERRORS_UNTIL_UNLOCK){
                    return false;
                }
                else if(metricsComparison.getTiltDelta() > BaselineUtil.MAX_TILT_ERRORS_UNTIL_UNLOCK){
                    return false;
                }
                else {
                    return true;
                }
            }
            catch (JSONException e) {
                // Ignore
            }
        }

        return false;
    }

    public IReward unlock(){
        return RewardsList.getInstance().nextReward();
    }

}
