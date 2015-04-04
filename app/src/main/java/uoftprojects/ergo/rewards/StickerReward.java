package uoftprojects.ergo.rewards;

/**
 * Created by harsha on 2015-04-04.
 */
public class StickerReward implements IReward {

    private int resourceId;
    private String name;

    public StickerReward(String name, int resourceId){
        this.name = name;
        this.resourceId = resourceId;
    }


    @Override
    public RewardType getType() {
        return RewardType.Sticker;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getResourceId(){
        return this.resourceId;
    }

}
