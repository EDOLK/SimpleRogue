package game.gameobjects.terrains.projectiles;

import java.util.List;

import org.hexworks.zircon.api.data.Tile;

import game.App;
import game.Dungeon;
import game.Line;
import game.gamelogic.Aimable;
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

    private ThrownItem(List<Space> list, int throwerStrength, Item item) {
        super(list.iterator());
        this.throwerStrength = throwerStrength;
        this.item = item;
    }

    public static ThrownItem throwItem(Entity thrower, Space toSpace, Item item){

        int perception = Skill.getSkill(Skill.PERCEPTION, thrower);
        int strength = Attribute.getAttribute(Attribute.STRENGTH, thrower);

        int maxDistAllowed = 6 + (strength * 3) - (item.getWeight()/5);

        List<Space> path = Line.getLineAsListInclusive(thrower.getSpace(), toSpace);

        int dist = Math.min(path.size(), maxDistAllowed);

        path = path.subList(0, dist);

        Space finalPathSpace = path.get(path.size()-1);

        int manDist = Space.manDist(thrower.getSpace(), finalPathSpace);
        int inaccuracyRange = (int)App.lerp(0, manDist/5, 20, 0, perception);

        int x = finalPathSpace.getX() + (App.randomNumber(-inaccuracyRange, inaccuracyRange));
        int y = finalPathSpace.getY() + (App.randomNumber(-inaccuracyRange, inaccuracyRange));

        path = Line.getLineAsListInclusive(
            thrower.getSpace(),
            Dungeon.getCurrentFloor().getClampedSpace(x, y)
        );
        path = path.subList(1, path.size());

        return new ThrownItem(path, strength, item);
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

    @Override
    public int getTimeToMove() {
        return (int)App.lerp(0, 30, 20, 5, throwerStrength);
    }

}
