package game.gameobjects.terrains.projectiles;

import java.util.Optional;

import game.gamelogic.Angle;
import game.gameobjects.Space;

public class SpaceAngleIterator extends AngleIterator {

    private Optional<Space> ls;

    public SpaceAngleIterator(Space origin, Angle angle) {
        super(origin, angle);
    }

    @Override
    public boolean hasNext() {
        if (ns != null) {
            boolean n = super.hasNext();
            while (ns.get() == ls.get()) {
                n = super.hasNext();
            }
            return n;
        }
        boolean n = super.hasNext();
        ls = ns;
        return n;
    }

    @Override
    public Space next() {
        return super.next();
    }
}
