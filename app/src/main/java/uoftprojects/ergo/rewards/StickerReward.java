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

    @Override
    public boolean equals(Object o) {
        if(o instanceof StickerReward){

            StickerReward stickerReward = (StickerReward)o;

            if(this.getName().equals(stickerReward.getName())  &&  this.getResourceId() == stickerReward.getResourceId()){
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return 31 * this.getName().hashCode() * this.getResourceId();
    }
}
