package game.gamelogic.behavior;

import java.util.List;
import java.util.Optional;

import game.gameobjects.Space;
import game.gameobjects.entities.Animal;

public class AnimalSearching extends AnimalWandering {

    protected Space lastSpace;

    public AnimalSearching(Animal animal, Space lastSpace) {
        super(animal);
        this.wanderRange = 1;
        this.lastSpace = lastSpace;
    }

    @Override
    public int behave() {
        int i = super.behave();
        Optional<? extends Behavior> done = getSearchDoneBehavior();
        if (done.isPresent()) {
            if (path == null) {
                return animal.setAndBehave(done.get());
            }
            if (locationInPath == path.length-1) {
                animal.setBehavior(done.get());
            }
        }
        return i;
    }

    @Override
    protected Optional<? extends Behavior> getWanderingBehavior() {
        return Optional.of(new AnimalSearching(animal, lastSpace));
    }

    protected Optional<? extends Behavior> getSearchDoneBehavior(){
        return super.getWanderingBehavior();
    }

    @Override
    protected List<Space> getWanderSpaces() {
        return super.getWanderSpaces(true);
    }

    @Override
    protected int getWanderCenterX() {
        return this.lastSpace.getX();
    }

    @Override
    protected int getWanderCenterY() {
        return this.lastSpace.getY();
    }

}
