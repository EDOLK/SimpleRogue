package game.gamelogic.behavior;

import static game.App.randomNumber;
import static game.Dungeon.getCurrentFloor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import game.PathConditions;
import game.PathTracker;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;

public class AnimalBehavior extends Behavior {

    private Entity animal;
    private Entity target;
    private int wanderRange = 10;
    private PathTracker huntingPathTracker;
    private PathTracker wanderingPathTracker;

    public AnimalBehavior(Entity animal) {
        this.animal = animal;
    }

    @Override
    public void behave() {
        if (target != null && !getEntitiesInVision().contains(target)) {
            Optional<PathTracker> tracker = PathTracker.createPathTracker(animal, target.getSpace(), generateConditionsToEntity());
            if (tracker.isPresent()) {
                this.wanderingPathTracker = tracker.get();
                this.huntingPathTracker = null;
                this.target = null;
            }
            wander();
            return;
        }
        if (huntingPathTracker != null && huntingPathTracker.nextSpaceAvailable()) {
            Space nextSpace = huntingPathTracker.getNextSpace();
            if (!Space.moveEntity(animal, nextSpace) && nextSpace.isOccupied() && nextSpace.getOccupant() == target){
                Floor.doAttack(animal,target);
            }
            return;
        } else {
            Entity prey = checkForTarget();
            if (prey != null) {
                this.target = prey;
                Optional<PathTracker> tracker = PathTracker.createPathTracker(animal, prey, generateConditionsToEntity());
                if (tracker.isPresent()) {
                    this.huntingPathTracker = tracker.get();
                    behave();
                    return;
                }
            }
            wander();
        }
    }

    protected void wander() {
        if (wanderingPathTracker != null && wanderingPathTracker.nextSpaceAvailable()) {
            Space.moveEntity(animal, wanderingPathTracker.getNextSpace());
            return;
        } else {
            Space space = getEmptyViableSpace();
            if (space != null){
                Optional<PathTracker> tracker = PathTracker.createPathTracker(animal,space,generateConditionsToSpace());
                if (tracker.isPresent()) {
                    this.wanderingPathTracker = tracker.get();
                    wander();
                    return;
                }
            }
        }
    }

    protected PathConditions generateConditionsToSpace(){
        return new PathConditions().addDeterrentConditions(
            (space) -> {
                return !space.getTerrains().isEmpty() ? 10d : 0d;
            }
        );
    }

    protected PathConditions generateConditionsToEntity(){
        return generateConditionsToSpace();
    }

    protected Entity checkForTarget(){
        for (Entity entity : getEntitiesInVision()) {
            if (entity instanceof PlayerEntity playerEntity){
                return playerEntity;
            }
        }
        return null;
    }

    protected List<Entity> getEntitiesInVision(){
        List<Entity> entityList = new ArrayList<Entity>();
        for (Space space : this.animal.getSpacesInVision()) {
            if (space.isOccupied()){
                entityList.add(space.getOccupant());
            }
        }
        return entityList;
    }

    protected Space getEmptyViableSpace(){
        Floor floor = getCurrentFloor();
        int yMin = floor.clampY(this.animal.getY() - wanderRange);
        int yMax = floor.clampY(this.animal.getY() + wanderRange);
        int xMin = floor.clampX(this.animal.getX() - wanderRange);
        int xMax = floor.clampX(this.animal.getX() + wanderRange);
        Space possibleSpace;
        do {
            possibleSpace = floor.getSpace(randomNumber(xMin, xMax), randomNumber(yMin, yMax));
        } while (possibleSpace.isOccupied());
        return possibleSpace.isOccupied() ? null : possibleSpace;
    }

}
