package game.gamelogic;

import java.util.Optional;

import game.gameobjects.entities.Entity;

public interface HasMemory {
    public void addToMemory(Entity entity);
    public void incrementMemory();
    public Optional<Integer> getFromMemory(Entity entity);
    default int getMaxMemoryTime(){
        return 15;
    }
    default int getRememberTime(){
        return 100;
    }
}
