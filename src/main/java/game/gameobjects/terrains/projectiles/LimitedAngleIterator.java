package game.gameobjects.terrains.projectiles;

import game.gamelogic.Angle;
import game.gameobjects.Space;

public class LimitedAngleIterator extends AngleIterator {

    private int countdown = 0;

    public LimitedAngleIterator(Space origin, Angle angle, int limit) {
        super(origin, angle);
        countdown = limit;
    }

    @Override
    public boolean hasNext() {
        return super.hasNext() && countdown > 0;
    }

    @Override
    public Space next() {
        Space next = super.next();
        countdown--;
        return next;
    }

}
