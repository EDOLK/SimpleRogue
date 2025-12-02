package game.gameobjects.terrains.projectiles;

import java.util.Iterator;
import java.util.Optional;

import game.Dungeon;
import game.gamelogic.Angle;
import game.gameobjects.Space;

public class AngleIterator implements Iterator<Space> {
    private final Angle angle;
    private final Space origin;
    private int i = 1;
    protected Optional<Space> ns;

    public AngleIterator(Space origin, Angle angle) {
        this.origin = origin;
        this.angle = angle;
        this.ns = Dungeon.getCurrentFloor().getSpaceByAngle(origin, angle, i++);
    }

    @Override
    public boolean hasNext() {
        Optional<Space> pns = Dungeon.getCurrentFloor().getSpaceByAngle(origin, angle, i);
        while (pns.isPresent() && ns.isPresent() && pns.get() == ns.get()) {
            i++;
            pns = Dungeon.getCurrentFloor().getSpaceByAngle(origin, angle, i);
        }
        ns = pns;
        i++;
        return ns.isPresent();
    }

    @Override
    public Space next() {
        return ns.get();
    }
}
