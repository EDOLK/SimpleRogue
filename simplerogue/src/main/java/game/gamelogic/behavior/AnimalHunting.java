package game.gamelogic.behavior;

import java.util.Optional;

import game.Path;
import game.PathConditions;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;

public class AnimalHunting extends Behavior {

    private Animal animal;
    private Entity target;
    private Space[] path;
    private int locationInPath = 0;

    private AnimalHunting(Animal animal, Entity target) {
        this.animal = animal;
        this.target = target;
    }

    public static Optional<AnimalHunting> createHunting(Animal animal, Entity target){
        try {
            AnimalHunting h = new AnimalHunting(animal, target);
            Space[] path = Path.getPathAsArray(animal.getSpace(), target.getSpace(), h.generateConditionsToEntity());
            h.path = path;
            return Optional.of(h);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public int behave() {
        if (this.path == null) {
            try {
                this.path = Path.getPathAsArray(animal.getSpace(), target.getSpace(), generateConditionsToEntity());
            } catch (Exception e) {
                return setAndReturn(new AnimalWandering(this.animal));
            }
        }
        if (path[locationInPath].getOccupant() == animal && path[path.length-1].getOccupant() == target && !pathIsBlocked()) 
        {
            Space possibleSpace = path[locationInPath+1];
            if (Space.moveEntity(animal, possibleSpace)) {
                locationInPath++;
                return animal.getTimeToMove();
            }
            Floor.doAttack(this.animal, this.target);
            return animal.getTimeToAttack();
        }
        Optional<AnimalHunting> hunting = createHunting(this.animal, this.target);
        if (hunting.isPresent()) {
            animal.setBehavior(hunting.get());
        } else {
            animal.setBehavior(new AnimalWandering(this.animal));
        }
        if (animal.isActive()) {
            return animal.behave();
        }
        return animal.getTimeToWait();
    }

    private int setAndReturn(Behavior newBehavior){
        this.animal.setBehavior(newBehavior);
        if (animal.isActive()) {
            return animal.behave();
        }
        return animal.getTimeToWait();
    }

    private boolean pathIsBlocked(){
        for (int i = locationInPath+1; i < path.length-1; i++) {
            Space space = path[i];
            if (generateConditionsToSpace().evaluateForForbidden(space)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isActive() {
        return this.animal.isAlive();
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
    
}
