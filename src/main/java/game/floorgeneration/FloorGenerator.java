package game.floorgeneration;

import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.floors.Floor;

public abstract class FloorGenerator {
    
    protected int depth;
    
    public FloorGenerator(int depth){
        this.depth = depth;
    }

    public abstract void generateFloor(Floor floor, PlayerEntity playerEntity);

}
