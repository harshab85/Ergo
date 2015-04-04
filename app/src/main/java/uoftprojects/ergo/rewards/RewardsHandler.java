package uoftprojects.ergo.rewards;

import org.json.JSONException;
import org.json.JSONObject;


import uoftprojects.ergo.metrics.usage.MetricsComparison;
import uoftprojects.ergo.metrics.usage.MetricsStorage;
import uoftprojects.ergo.util.BaselineUtil;
import uoftprojects.ergo.util.SetupUtil;

/**
 * Created by harsha on 2015-04-04.
 */
public final class RewardsHandler {

    private JSONObject initialMetrics;

    private RewardsList rewardsList = new RewardsList();

    public RewardsHandler(JSONObject initialMetrics){
        this.initialMetrics = initialMetrics;
    }

    public boolean shouldUnlockReward(){
        if(!SetupUtil.hasWatchedFirstVideo()){
            SetupUtil.watchedFirstVideo();
            return true;
        }
        else{
            try {
                JSONObject currentMetrics = MetricsStorage.getInstance().getCurrentMetrics();
                MetricsComparison metricsComparison = MetricsStorage.getInstance().compare(currentMetrics, initialMetrics);
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
        return rewardsList.next();
    }

}
