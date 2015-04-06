package uoftprojects.ergo.rewards;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import uoftprojects.ergo.R;
import uoftprojects.ergo.util.StorageUtil;

/**
 * Created by harsha on 2015-04-04.
 */
public final class RewardsList {

    public static final String USED_REWARDS_KEY = "usedRewards";

    private static List<IReward> rewardsList = new ArrayList<>();
    private static List<IReward> usedRewards = new ArrayList<>();
    private static Iterator<IReward> rewardsIterator;

    private static final RewardsList INSTANCE = new RewardsList();

    private RewardsList(){
        // get all stickers from project (ensure first reward sticker is in top)
        List<IReward> rewards = loadVideos();

        // get used stickers from shared pref
        String uRewards = StorageUtil.getString(USED_REWARDS_KEY);
        try {
            if(uRewards != null && !uRewards.isEmpty()) {
                JSONArray uRewardsArray = new JSONArray(uRewards);
                for(int i=0; i<uRewardsArray.length(); i++){
                    int resourceId = uRewardsArray.getInt(i);
                    IReward reward = new StickerReward("", resourceId);
                    usedRewards.add(reward);
                }
            }
        }
        catch (JSONException e) {
            // Ignore
        }

        // remove used stickers from first list
        rewards.removeAll(usedRewards);

        // save other stickers in rewards list
        rewardsList.addAll(rewards);

        rewardsIterator = rewardsList.iterator();
    }

    public static RewardsList getInstance(){
        return INSTANCE;
    }

    public IReward nextReward(){
        if(rewardsIterator.hasNext()){
            IReward reward = rewardsIterator.next();
            usedRewards.add(reward);
            return reward;
        }
        else{
            return null;
        }
    }

    public void store(){
        JSONArray usedRewardsArray = new JSONArray();
        for(IReward reward: usedRewards) {
            if(reward instanceof  StickerReward) {
                usedRewardsArray.put(((StickerReward)reward).getResourceId());
            }
        }

        StorageUtil.addString(USED_REWARDS_KEY, usedRewardsArray.toString());
    }

    private static List<IReward> loadVideos(){
        List<IReward> videos = new ArrayList<>();

        int imageIds[] = new int[]
                {R.drawable.sticker_explorer, R.drawable.sticker_peek_a_boo_1,
                 R.drawable.sticker_peek_a_boo_2, R.drawable.sticker_peek_a_boo_3,
                 R.drawable.sticker_peek_a_boo_4, R.drawable.sticker_peek_a_boo_5,
                 R.drawable.sticker_sunglasses, R.drawable.sticker_winter};

        for(int i=0; i<imageIds.length; i++){
            IReward reward = new StickerReward("", imageIds[i]);
            videos.add(reward);
        }

        return videos;
    }



}
