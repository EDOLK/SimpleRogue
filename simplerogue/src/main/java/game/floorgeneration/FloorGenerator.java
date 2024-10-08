package game.floorgeneration;

import static game.App.randomNumber;

import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;

public abstract class FloorGenerator {
    
    protected int depth;
    protected int points;
    
    public FloorGenerator(int depth){
        this.depth = depth;
    }

    public abstract void generateFloor(Space[][] spaces, PlayerEntity playerEntity);
    
    
    protected Space getRandomSpace(Space[][] spaces){
        return spaces[randomNumber(0, spaces.length-1)][randomNumber(0, spaces.length-1)];
    }
    
    protected Space getRandomUnoccupiedSpace(Space[][] spaces){
        Space randomSpace = getRandomSpace(spaces);
        while (randomSpace.isOccupied()){
            randomSpace = getRandomSpace(spaces);
        }
        return randomSpace;
    }

    @SafeVarargs
    protected final Space getRandomUnoccupiedSpace(Space[][] spaces, Class<? extends Entity>... entityClass){
        Space randomSpace = getRandomSpace(spaces);
        outer:
        while (randomSpace.isOccupied()){
            for (Class<? extends Entity> class1 : entityClass) {
                if (class1.isInstance(randomSpace.getOccupant())){
                    break;
                }
                break outer;
            }
            randomSpace = getRandomSpace(spaces);
        }
        return randomSpace;
    }
}
