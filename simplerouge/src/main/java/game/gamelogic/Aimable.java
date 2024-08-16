package game.gamelogic;

import game.gameobjects.Space;
import game.gameobjects.entities.Entity;

public interface Aimable{
    public void onHit(Entity target);
    public void onLand(Space space);
    public boolean landsOnHit();
}
