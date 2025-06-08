package game.gamelogic.behavior;

import java.util.Optional;

import game.Path;
import game.Path.PathNotFoundException;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.BeginningSearch;

public class AnimalHunting extends Behavior {

    protected Animal animal;
    protected Entity target;
    protected Space[] path;
    protected int locationInPath = 0;

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
        if (!animal.isWithinVision(target)) {
            Optional<? extends Behavior> wanderToTarget = getSearchingBehavior(target.getSpace());
            if (wanderToTarget.isPresent()) {
                animal.addStatus(new BeginningSearch());
                return animal.setAndBehave(wanderToTarget.get());
            }
        }
        if (this.isValid()) {
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

    protected Optional<? extends Behavior> getSearchingBehavior(Space space){
        return Optional.of(new AnimalSearching(animal, space));
    }

    protected Optional<? extends Behavior> getWanderingBehavior(){
        return Optional.of(new AnimalWandering(animal));
    }

    protected Optional<? extends Behavior> getHuntingBehavior(Entity target){
        try {
            AnimalHunting h = new AnimalHunting(animal, target);
            //TODO: Bandaid fix. Fix properly later.
            if (!h.isValid()) {
                return Optional.empty();
            }
            return Optional.of(h);
        } catch (Exception e) {

        }
        return Optional.empty();
    }

    public boolean isValid(){
        return this.path[this.locationInPath].getOccupant() == this.animal && this.path[this.path.length-1].getOccupant() == this.target && !this.pathIsBlocked();
    }

    protected boolean pathIsBlocked(){
        for (int i = locationInPath+1; i < path.length-1; i++) {
            Space space = path[i];
            if (this.animal.getConditionsToSpace().evaluateForForbidden(space)) {
                return true;
            }
        }
        return false;
    }
    
}
