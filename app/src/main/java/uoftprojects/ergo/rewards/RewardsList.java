package uoftprojects.ergo.rewards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by harsha on 2015-04-04.
 */
public final class RewardsList {

    private static List<IReward> rewardsList = new ArrayList<>();
    private List<IReward> usedRewards = new ArrayList<>();
    private static Iterator<IReward> rewardsIterator;

    static{

        // get all stickers from project (ensure first reward sticker is in top)

        // get used stickers

        // remove used stickers from first list

        // save other stickers in rewards list

        rewardsIterator = rewardsList.iterator();
    }

    public IReward next(){
        if(rewardsIterator.hasNext()){
            IReward reward = rewardsIterator.next();
            usedRewards.add(reward);
            return reward;
        }
        else{
            return null;
        }
    }

}
