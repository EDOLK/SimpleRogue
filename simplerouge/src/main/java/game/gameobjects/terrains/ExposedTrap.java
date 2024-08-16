package game.gameobjects.terrains;

import game.gamelogic.Examinable;

public abstract class ExposedTrap extends Trap implements Examinable{
    public abstract boolean isDestroyedOnTrigger();
}
