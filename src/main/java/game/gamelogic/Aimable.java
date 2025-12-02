package game.gamelogic;

import game.gameobjects.Space;

public interface Aimable{

    public boolean collides(Space space);

    public void onCollision(Space beforeSpace, Space collidingSpace);

    public void onLand(Space space);
}
