package game.gamelogic.passives;

import game.gamelogic.HasMemory;
import game.gamelogic.abilities.Passive;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.entities.Entity;

public class MemoryIncrementor implements Passive, Behavable{

    private HasMemory owner;

    public MemoryIncrementor(HasMemory owner) {
        this.owner = owner;
    }

    @Override
    public int behave() {
        owner.incrementMemory();
        return owner.getRememberTime();
    }

    @Override
    public boolean isActive() {
        return owner != null && (owner instanceof Entity entity ? entity.isAlive() : true);
    }

    
}
