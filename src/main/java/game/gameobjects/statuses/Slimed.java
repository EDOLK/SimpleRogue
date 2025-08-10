package game.gameobjects.statuses;

import game.gamelogic.time.ModifiesAttackTime;
import game.gamelogic.time.ModifiesMoveTime;
import game.gameobjects.terrains.liquids.Liquid;
import game.gameobjects.terrains.liquids.SlimeLiquid;
import game.gameobjects.terrains.liquids.Water;

public class Slimed extends Wet implements ModifiesMoveTime, ModifiesAttackTime {

    public Slimed(SlimeLiquid liquid){
        this((Liquid)liquid);
    }

    private Slimed(Liquid liquid) {
        super(liquid);
        setDescriptor("Slimed");
    }

    @Override
    public int modifyAttackTime(int time) {
        return time + (int)(time * 0.25);
    }

    @Override
    public int modifyMoveTime(int time) {
        return time + (int)(time * 0.25);
    }

    @Override
    public boolean filterIn(Status status) {
        if (status instanceof Wet wet && wet.getLiquid() instanceof Water) {
            wet.getOwner().removeStatus(wet);
            return true;
        }
        return super.filterIn(status);
    }

    @Override
    public boolean filterOut(Status status) {
        if (status instanceof Wet wet && wet.getLiquid() instanceof Water) {
            this.owner.removeStatus(this);
            return true;
        }
        return super.filterOut(status);
    }

}
