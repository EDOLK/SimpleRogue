package game.gamelogic.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import game.App;
import game.Dungeon;
import game.Path;
import game.gameobjects.Space;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;

public class AnimalWandering extends Behavior {

    protected Animal animal;
    protected Space[] path;
    protected int locationInPath = 0;
    protected int wanderRange = 7;

    public AnimalWandering(Animal animal) {
        this.animal = animal;
    }

    @Override
    public int behave() {
        Optional<? extends Behavior> hunt = checkForHunt();
        if (hunt.isPresent()) {
            return animal.setAndBehave(hunt.get());
        }
        if (path == null && !generatePath()) {
            System.err.println("Error: no path found");
            return animal.getTimeToWait();
        }
        if (path[locationInPath].getOccupant() == animal && !pathIsBlocked() && locationInPath != path.length-1) {
            Space possibleSpace = path[locationInPath+1];
            if (Space.moveEntity(animal, possibleSpace)) {
                locationInPath++;
                return animal.getTimeToMove();
            }
        } else {
            Optional<? extends Behavior> wandering = getWanderingBehavior();
            if (wandering.isPresent()) {
                return animal.setAndBehave(wandering.get());
            }
        }
        return animal.getTimeToWait();
    }

    @Override
    public boolean isActive() {
        return animal.isAlive();
    }

    protected Optional<? extends Behavior> getHuntingBehavior(Entity target){
        try {
            AnimalHunting h = new AnimalHunting(this.animal, target);
            //TODO: Bandaid fix. Fix properly later.
            if (!h.getPath()[h.getPath().length-1].isOccupied() || h.getPath()[h.getPath().length-1].getOccupant() != target) {
                return Optional.empty();
            }
            return Optional.of(new AnimalHunting(this.animal, target));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    protected Optional<? extends Behavior> getWanderingBehavior(){
        return Optional.of(new AnimalWandering(animal));
    }

    protected Optional<? extends Behavior> checkForHunt() {
        List<Entity> potentialTargets = new ArrayList<>();
        for (Entity entity : this.animal.getEntitiesInVision()) {
            if (animal.isEnemy(entity)) {
                potentialTargets.add(entity);
            }
        }
        while (!potentialTargets.isEmpty()) {
            Entity random = App.getRandom(potentialTargets);
            Optional<? extends Behavior> hunting = getHuntingBehavior(random);
            if (hunting.isPresent()) {
                return hunting;
            } else {
                potentialTargets.remove(random);
            }
        }
        return Optional.empty();
    }

    private boolean generatePath(){
        List<Space> potentialSpaces = getWanderSpaces();
        while (path == null && !potentialSpaces.isEmpty()) {
            Space p = App.getRandom(potentialSpaces);
            try {
                path = Path.getPathAsArray(animal.getSpace(), p, animal.getConditionsToSpace());
            } catch (Exception e) {
                potentialSpaces.remove(p);
            }
        }
        return path != null;
    }

    protected List<Space> getWanderSpaces(){
        List<Space> potentialSpaces = new ArrayList<>();
        int ax = getWanderCenterX();
        int ay = getWanderCenterY();
        int minX = Dungeon.getCurrentFloor().clampX(ax-wanderRange);
        int maxX = Dungeon.getCurrentFloor().clampX(ax+wanderRange);
        int minY = Dungeon.getCurrentFloor().clampY(ay-wanderRange);
        int maxY = Dungeon.getCurrentFloor().clampY(ay+wanderRange);
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                if (x == ax && y == ay)
                    continue;
                potentialSpaces.add(Dungeon.getCurrentFloor().getSpace(x,y));
            }
        }
        for (int i = 0; i < potentialSpaces.size(); i++) {
            if (potentialSpaces.get(i).isOccupied()) {
                potentialSpaces.remove(i);
                i--;
            }
        }
        return potentialSpaces;
    }

    protected int getWanderCenterY() {
        return this.animal.getY();
    }

    protected int getWanderCenterX() {
        return this.animal.getX();
    }

    private boolean pathIsBlocked(){
        for (int i = locationInPath+1; i < path.length; i++) {
            Space space = path[i];
            if (this.animal.getConditionsToSpace().evaluateForForbidden(space)) {
                return true;
            }
        }
        return false;
    }

}
