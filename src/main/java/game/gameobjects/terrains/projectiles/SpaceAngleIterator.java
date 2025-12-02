package game.gameobjects.terrains.projectiles;

import game.gamelogic.Angle;
import game.gameobjects.Space;

public class SpaceAngleIterator extends AngleIterator {

    private Space limitSpace;

    public SpaceAngleIterator(Space origin, Angle angle, Space limitSpace) {
        super(origin, angle);
        this.limitSpace = limitSpace;
    }

    @Override
    public boolean hasNext() {
        return super.hasNext() && ns.get() != limitSpace;
    }

    @Override
    public Space next() {
        return super.next();
    }

    
}
