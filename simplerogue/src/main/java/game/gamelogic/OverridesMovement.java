package game.gamelogic;

import game.gameobjects.Space;
import game.gameobjects.entities.Entity;

public interface OverridesMovement {
    public boolean overrideMovement(Entity entity, Space toSpace);
    default boolean isEnabled(){
        return true;
    };
}
