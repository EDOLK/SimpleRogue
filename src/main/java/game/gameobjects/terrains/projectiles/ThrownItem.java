package game.gameobjects.terrains.projectiles;

import java.util.Iterator;

import org.hexworks.zircon.api.data.Tile;

import game.App;
import game.gamelogic.Aimable;
import game.gamelogic.Angle;
import game.gamelogic.Attribute;
import game.gamelogic.Examinable;
import game.gamelogic.LightSource;
import game.gamelogic.Skill;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;

public class ThrownItem extends Projectile implements LightSource, Examinable{

    private Item item;
    private Integer throwerStrength;

    private ThrownItem(Space origin, Iterator<Space> spaceIterator, Item item) {
        super(origin, spaceIterator);
        this.item = item;
    }

    // FIXME: Overshoots by one when perception is 20
    public static ThrownItem throwItem(Entity thrower, Space toSpace, Item item){
        int perception = Skill.getSkill(Skill.PERCEPTION, thrower);
        int inaccuracy = (int)App.lerp(0, 45, 20, 0, perception);
        int variation = App.randomNumber(inaccuracy*-1, inaccuracy);
        int strength = Attribute.getAttribute(Attribute.STRENGTH, thrower);
        int timeToMove = (int)App.lerp(0, 30, 20, 5, strength);
        Space origin = thrower.getSpace();
        Angle angle = Angle.of(origin, toSpace);
        angle.setDegree(angle.getDegree() + variation);
        int dist = Math.min(Space.euclidDist(origin, toSpace), 5 + (strength * 3) - (item.getWeight()/5));
        ThrownItem t = new ThrownItem(origin, new LimitedAngleIterator(origin, angle, dist), item);
        t.throwerStrength = strength;
        t.timeToMove = timeToMove;
        return t;
    }

    @Override
    public boolean collides(Space space) {
        if (item instanceof Aimable aimable) {
            return aimable.collides(space);
        }
        return space.isOccupied();
    }

    @Override
    public void onCollision(Space beforeSpace, Space collidingSpace) {
        if (item instanceof Aimable aimable) {
            aimable.onCollision(beforeSpace, collidingSpace);
            return;
        }
        collidingSpace.getOccupant().dealDamage((int)Math.log(item.getWeight()) * throwerStrength, DamageType.BLUNT);
        beforeSpace.addItem(item);
    }

    @Override
    public void onLand(Space space) {
        if (item instanceof Aimable aimable) {
            aimable.onLand(space);
            return;
        }
        space.addItem(item);
    }

    @Override
    public Tile getTile(double percent) {
        if (item != null) {
            return item.getTile(percent);
        }
        return super.getTile(percent);
    }

    @Override
    public int getLightSourceIntensity() {
        if (item instanceof LightSource lsItem) {
            return lsItem.getLightSourceIntensity();
        }
        return 0;
    }

    @Override
    public String getName() {
        return "Thrown " + item.getName();
    }

    @Override
    public String getDescription() {
        return item.getDescription();
    }

}
