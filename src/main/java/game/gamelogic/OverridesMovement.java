package game.gamelogic;

import game.gameobjects.MovementResult;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;

public interface OverridesMovement {
    public MovementResult overrideMovement(MovementResult result, Entity entity, Space toSpace);
    default boolean isEnabled(){
        return true;
    };
}
