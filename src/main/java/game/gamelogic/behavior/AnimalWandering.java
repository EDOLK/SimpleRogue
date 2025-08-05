package game.gamelogic.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import game.App;
import game.Dungeon;
import game.Path;
import game.gamelogic.Skill;
import game.gameobjects.Space;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.BeginningHunt;

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
            animal.addStatus(new BeginningHunt());
            return animal.setAndBehave(hunt.get());
        }
        if (path == null && !generatePath()) {
            return animal.getTimeToWait();
        }
        if (path[locationInPath].getOccupant() == animal && !pathIsBlocked() && locationInPath != path.length-1) {
            Space possibleSpace = path[locationInPath+1];
            if (Space.moveEntity(animal, possibleSpace).isSuccessful()) {
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
            if (!h.pathIsValid()) {
                return Optional.empty();
            }
            return Optional.of(h);
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
            if (animal.isEnemy(entity) && checkForStealth(entity)) {
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

    private boolean checkForStealth(Entity entity) {
        if (entity != null) {
            int entityStealth = App.randomNumber(1,20);
            int animalPerception = 10;
            entityStealth += Skill.getSkill(Skill.STEALTH, entity);
            animalPerception += Skill.getSkill(Skill.PERCEPTION, animal);
            entityStealth += (int)((entity.getSpace().getLight()-0.50f)*-15);
            int distance = Space.getDistance(animal.getSpace(), entity.getSpace());
            if (distance <= 5) {
                entityStealth -= Math.abs(distance-6);
            }
            if (animalPerception >= entityStealth) {
                return true;
            }
        }
        return false;
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
        return getWanderSpaces(false);
    }

    protected List<Space> getWanderSpaces(boolean inclusive){
        List<Space> potentialSpaces = new ArrayList<>();
        int ax = getWanderCenterX();
        int ay = getWanderCenterY();
        int minX = Dungeon.getCurrentFloor().clampX(ax-wanderRange);
        int maxX = Dungeon.getCurrentFloor().clampX(ax+wanderRange);
        int minY = Dungeon.getCurrentFloor().clampY(ay-wanderRange);
        int maxY = Dungeon.getCurrentFloor().clampY(ay+wanderRange);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (x == ax && y == ay && !inclusive)
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
