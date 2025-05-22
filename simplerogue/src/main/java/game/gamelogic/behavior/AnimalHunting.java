package game.gamelogic.behavior;

import java.util.Optional;

import game.Path;
import game.Path.PathNotFoundException;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;

public class AnimalHunting extends Behavior {

    private Animal animal;
    private Entity target;
    private Space[] path;
    private int locationInPath = 0;

    public AnimalHunting(Animal animal, Entity target) throws PathNotFoundException {
        this.animal = animal;
        this.target = target;
        this.path = Path.getPathAsArray(animal.getSpace(), target.getSpace(), animal.getConditionsToEntity());
    }

    public Space[] getPath() {
        return path;
    }

    @Override
    public int behave() {
        if (path[locationInPath].getOccupant() == animal && path[path.length-1].getOccupant() == target && !pathIsBlocked()) {
            Space possibleSpace = path[locationInPath+1];
            if (Space.moveEntity(animal, possibleSpace)) {
                locationInPath++;
                return animal.getTimeToMove();
            }
            if (possibleSpace.isOccupied() && possibleSpace.getOccupant() == target) {
                Floor.doAttack(animal, target);
                return animal.getTimeToAttack();
            }
        }
        Optional<? extends Behavior> hunting = getHuntingBehavior(target);
        if (hunting.isPresent()) {
            return animal.setAndBehave(hunting.get());
        }
        Optional<? extends Behavior> wandering = getWanderingBehavior();
        if (wandering.isPresent()) {
            return animal.setAndBehave(wandering.get());
        }
        return animal.getTimeToWait();
    }

    @Override
    public boolean isActive() {
        return this.animal.isAlive();
    }

    protected Optional<? extends Behavior> getWanderingBehavior(){
        return Optional.of(new AnimalWandering(animal));
    }

    protected Optional<? extends Behavior> getHuntingBehavior(Entity target){
        try {
            AnimalHunting h = new AnimalHunting(animal, target);
            //TODO: Bandaid fix. Fix properly later.
            if (!h.path[h.path.length-1].isOccupied() || h.path[h.path.length-1].getOccupant() != target) {
                return Optional.empty();
            }
            return Optional.of(h);
        } catch (Exception e) {

        }
        return Optional.empty();
    }

    private boolean pathIsBlocked(){
        for (int i = locationInPath+1; i < path.length-1; i++) {
            Space space = path[i];
            if (this.animal.getConditionsToSpace().evaluateForForbidden(space)) {
                return true;
            }
        }
        return false;
    }
    
}
