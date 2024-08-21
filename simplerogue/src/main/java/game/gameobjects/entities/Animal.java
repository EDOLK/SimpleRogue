package game.gameobjects.entities;

import static game.App.randomNumber;
import static game.Dungeon.getCurrentFloor;

import org.hexworks.zircon.api.color.TileColor;

import game.PathFinder;
import game.gamelogic.behavior.Behavior;
import game.gamelogic.behavior.HasBehavior;
import game.gameobjects.Floor;
import game.gameobjects.Space;

public abstract class Animal extends Entity implements HasBehavior{

    private Behavior behavior = new AnimalBehavior(this);

    public Animal(){
        super();
    }

    public Animal(TileColor bgColor, TileColor fGColor, char character) {
        super(bgColor, fGColor, character);
    }

    @Override
    public void defaultInteraction(Entity entity) {
        Floor.doAttack(entity, this);
    }

    @Override
    public Behavior getBehavior() {
        return behavior;
    }

    @Override
    public boolean setBehavior(Behavior behavior) {
        this.behavior = behavior;
        return true;
    }

    /**
     * AnimalBehavior
     */
    protected class AnimalBehavior extends Behavior{

        private Space wanderSpace;
        private PathFinder pathFinder;
        private int wanderRange = 10;
        private Animal animal;
        private Entity currentTarget;
        
        protected AnimalBehavior(Animal animal){
            this.animal = animal;
        }

        protected void setWanderRange(int wanderRange) {
            this.wanderRange = wanderRange;
        }

        protected Space getEmptyViableSpace(){
            int yMin = animal.getY() - wanderRange;
            int yMax = animal.getY() + wanderRange;
            int xMin = animal.getX() - wanderRange;
            int xMax = animal.getX() + wanderRange;
            if (yMin < 0){
                yMin = 0;
            }
            if (yMax >= getCurrentFloor().SIZE_Y){
                yMax = getCurrentFloor().SIZE_Y-1;
            }
            if (xMin < 0){
                xMin = 0;
            }
            if (xMax >= getCurrentFloor().SIZE_X){
                xMax = getCurrentFloor().SIZE_X-1;
            }
            Space possibleSpace = getCurrentFloor().getSpace(randomNumber(xMin, xMax), randomNumber(yMin, yMax));
            while (possibleSpace.isOccupied()) {
                possibleSpace = getCurrentFloor().getSpace(randomNumber(xMin, xMax), randomNumber(yMin, yMax));
            }
            return possibleSpace;
        }

        protected Entity checkForTarget(){
            for (Space space : animal.getSpacesInVision()) {
                if (space.isOccupied() && space.getOccupant() instanceof PlayerEntity){
                    return space.getOccupant();
                }
            }
            return null;
        }

        @Override
        public void behave() {
            currentTarget = checkForTarget();
            if (currentTarget != null){
                try {
                    pathFinder = new PathFinder(getSpace(), currentTarget.getSpace());
                    pathFinder.iterate();
                } catch (Exception e) {
                    wander();
                }
            }
            if (pathFinder != null){
                Space nextSpace = pathFinder.getSpace();
                if (Space.moveEntity(animal, nextSpace)){
                    if (!pathFinder.pathHasEnded()){
                        pathFinder.iterate();
                    }
                } else {
                    if (nextSpace.isOccupied() && nextSpace.getOccupant() == currentTarget){
                        Floor.doAttack(animal, currentTarget);
                    } else {
                        wander();
                    }
                }
            } else {
                wander();
            }
            if (pathFinder.pathHasEnded()){
                pathFinder = null;
            }
        }
        
        protected void wander(){
            wanderSpace = getEmptyViableSpace();
            try {
                pathFinder = new PathFinder(getSpace(), wanderSpace);
                pathFinder.iterate();
            } catch (Exception e) {
                wander();
            }
        }
        
    }
    @Override
    public boolean isActive() {
        return HasBehavior.super.isActive() && isAlive();
    }
}
