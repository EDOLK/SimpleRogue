package game.gameobjects.terrains.projectiles;

import java.util.Iterator;
import java.util.Optional;

import game.Dungeon;
import game.gamelogic.Angle;
import game.gameobjects.Space;

public class AngleIterator implements Iterator<Space> {
    protected final Angle angle;
    protected final Space origin;
    protected int i = 1;
    protected Optional<Space> ns = Optional.empty();

    public AngleIterator(Space origin, Angle angle) {
        this.origin = origin;
        this.angle = angle;
    }

    @Override
    public boolean hasNext() {
        ns = Dungeon.getCurrentFloor().getSpaceByAngle(origin, angle, i++);
        return this.ns.isPresent();
    }

    @Override
    public Space next() {
        return ns.get();
    }
}
