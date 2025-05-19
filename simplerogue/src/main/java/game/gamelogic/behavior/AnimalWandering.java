package game.gamelogic.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import game.App;
import game.Dungeon;
import game.Path;
import game.Path.PathNotFoundException;
import game.PathConditions;
import game.gameobjects.Space;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;

public class AnimalWandering extends Behavior {

    private Animal animal;
    private Space[] path;
    private int locationInPath = 0;
    private int wanderRange = 5;

    public AnimalWandering(Animal animal) {
        this.animal = animal;
    }

    private boolean generatePath(Animal animal){
        List<Space> potentialSpaces = getSpaces();
        while (path == null && !potentialSpaces.isEmpty()) {
            Space p = App.getRandom(potentialSpaces);
            try {
                path = Path.getPathAsArray(animal.getSpace(), p, generateConditionsToSpace());
            } catch (Exception e) {
                potentialSpaces.remove(p);
            }
        }
        return path != null;
    }

    private List<Space> getSpaces(){
        List<Space> potentialSpaces = new ArrayList<>();
        int ax = this.animal.getX();
        int ay = this.animal.getY();
        int minX = Dungeon.getCurrentFloor().clampX(ax-wanderRange);
        int maxX = Dungeon.getCurrentFloor().clampX(ax+wanderRange);
        int minY = Dungeon.getCurrentFloor().clampY(ay-wanderRange);
        int maxY = Dungeon.getCurrentFloor().clampY(ay+wanderRange);
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
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

    @Override
    public int behave() {
        List<Entity> potentialTargets = new ArrayList<>();
        for (Entity entity : getEntitiesInVision()) {
            if (this.animal.isEnemy(entity)) {
                potentialTargets.add(entity);
            }
        }
        while (!potentialTargets.isEmpty()) {
            Entity random = App.getRandom(potentialTargets);
            Optional<AnimalHunting> hunting = AnimalHunting.createHunting(this.animal, random);
            if (hunting.isPresent()) {
                return setAndReturn(hunting.get());
            } else {
                potentialTargets.remove(random);
            }
        }
        if (path == null) {
            if (!generatePath(animal)) {
                System.err.println("Error: no path found");
                return animal.getTimeToWait();
            }
        }
        if (locationInPath == path.length-1) {
            return setAndReturn(new AnimalWandering(this.animal));
        }
        if (path[locationInPath].getOccupant() == animal && !pathIsBlocked()) {
            Space possibleSpace = path[locationInPath+1];
            if (Space.moveEntity(animal, possibleSpace)) {
                locationInPath++;
                return animal.getTimeToMove();
            }
        } else {
            return setAndReturn(new AnimalWandering(this.animal));
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

    private List<Entity> getEntitiesInVision(){
        List<Entity> list = new ArrayList<>();
        for (Space space : this.animal.getSpacesInVision()) {
            if (space.isOccupied()) {
                list.add(space.getOccupant());
            }
        }
        return list;
    }

    @Override
    public boolean isActive() {
        return this.animal.isAlive();
    }

    private boolean pathIsBlocked(){
        for (int i = locationInPath+1; i < path.length; i++) {
            Space space = path[i];
            if (generateConditionsToSpace().evaluateForForbidden(space)) {
                return true;
            }
        }
        return false;
    }

    protected PathConditions generateConditionsToSpace(){
        return new PathConditions().addDeterrentConditions(
            (space) -> {
                return !space.getTerrains().isEmpty() ? 10d : 0d;
            }
        );
    }

}
