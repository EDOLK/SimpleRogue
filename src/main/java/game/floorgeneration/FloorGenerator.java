package game.floorgeneration;

import game.gameobjects.Space;
import game.gameobjects.entities.PlayerEntity;

public abstract class FloorGenerator {
    
    protected int depth;
    
    public FloorGenerator(int depth){
        this.depth = depth;
    }

    public abstract void generateFloor(Space[][] spaces, PlayerEntity playerEntity);

}
