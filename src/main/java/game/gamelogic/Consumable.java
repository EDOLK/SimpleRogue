package game.gamelogic;

import game.gameobjects.entities.Entity;

public interface Consumable {
    public boolean consume(Entity consumer);
    default int getSatiety(){
        return 0;
    };
}
