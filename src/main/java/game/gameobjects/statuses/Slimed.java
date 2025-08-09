package game.gameobjects.statuses;

import game.gamelogic.time.ModifiesAttackTime;
import game.gamelogic.time.ModifiesMoveTime;
import game.gameobjects.terrains.liquids.Liquid;
import game.gameobjects.terrains.liquids.SlimeLiquid;

public class Slimed extends Wet implements ModifiesMoveTime, ModifiesAttackTime {

    public Slimed(SlimeLiquid liquid){
        this((Liquid)liquid);
    }

    private Slimed(Liquid liquid) {
        super(liquid);
    }

    @Override
    public int modifyAttackTime(int time) {
        return time + (int)(time * 0.25);
    }

    @Override
    public int modifyMoveTime(int time) {
        return time + (int)(time * 0.25);
    }

}
