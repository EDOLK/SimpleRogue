package game.gameobjects.terrains.projectiles;

import java.util.Iterator;
import java.util.List;

import game.gamelogic.Angle;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.terrains.Terrain;

public abstract class Projectile extends Terrain implements Behavable, SelfAware {

    private Iterator<Space> spaceIterator;
    
    private Space space;

    protected int timeToMove;

    public Projectile(Space origin, Iterator<Space> spaceIterator){
        origin.addTerrain(this);
        this.spaceIterator = spaceIterator;
    }

    public Projectile(Space origin, Angle angle, int limit){
        this(origin, new LimitedAngleIterator(origin, angle, limit));
    };

    public Projectile(Space origin, Angle angle){
        this(origin, new AngleIterator(origin, angle));
    };

    public Projectile(List<Space> path){
        this(path.get(0), path.subList(1, path.size()).iterator());
    };

    @Override
    public int behave() {
        Space currSpace = getSpace();
        currSpace.removeTerrain(this);
        setSpace(null);
        if (spaceIterator.hasNext()) {
            Space nextSpace = spaceIterator.next();
            if (collides(nextSpace)) {
                onCollision(currSpace, nextSpace);
            } else {
                currSpace.addTerrain(
                    new ProjectileTrail(
                        getTile().getForegroundColor(),
                        Angle.of(currSpace, nextSpace),
                        currSpace
                    )
                );
                nextSpace.addTerrain(this);
            }
        } else {
            onLand(currSpace);
        }
        return timeToMove;
    }

    @Override
    public boolean isActive() {
        return getSpace() != null;
    }

    @Override
    public Space getSpace() {
        return this.space;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    public abstract boolean collides(Space space);

    public abstract void onCollision(Space beforeSpace, Space collidingSpace);

    public abstract void onLand(Space space);

}
