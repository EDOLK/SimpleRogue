package game.gamelogic;

import game.gameobjects.entities.Entity;
public interface Triggerable{
    public void triggerOnEntity(Entity entity);
    public boolean triggerableWhenAdding();
}
