package game.gameobjects.terrains.projectiles;

import java.util.Iterator;

import game.gamelogic.Angle;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.terrains.Terrain;

public abstract class Projectile extends Terrain implements Behavable, SelfAware {

    private Iterator<Space> spaceIterator;
    
    private Space space;

    public Projectile(Iterator<Space> spaceIterator){
        this.spaceIterator = spaceIterator;
    }

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
        return getTimeToMove();
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

    public Iterator<Space> getSpaceIterator() {
        return spaceIterator;
    }

    public void setSpaceIterator(Iterator<Space> spaceIterator) {
        this.spaceIterator = spaceIterator;
    }

    public abstract int getTimeToMove();

    public abstract boolean collides(Space space);

    public abstract void onCollision(Space beforeSpace, Space collidingSpace);

    public abstract void onLand(Space space);

}
