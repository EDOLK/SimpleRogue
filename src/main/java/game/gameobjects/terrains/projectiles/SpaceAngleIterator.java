package game.gameobjects.terrains.projectiles;

import java.util.Optional;

import game.gamelogic.Angle;
import game.gameobjects.Space;

public class SpaceAngleIterator extends AngleIterator {

    private Optional<Space> ls = Optional.empty();

    public SpaceAngleIterator(Space origin, Angle angle) {
        super(origin, angle);
        ls = Optional.of(origin);
    }

    @Override
    public boolean hasNext() {
        boolean n = super.hasNext();
        while (ls.get() == ns.get()) {
            n = super.hasNext();
        }
        ls = ns;
        return n;
    }

    @Override
    public Space next() {
        return super.next();
    }
}
