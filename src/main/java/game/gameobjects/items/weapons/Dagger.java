package game.gameobjects.items.weapons;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Aimable;
import game.gameobjects.DamageType;
import game.gameobjects.Space;

public class Dagger extends Weapon implements Aimable {
    public Dagger(){
        super(TileColor.transparent(), TileColor.create(153, 153, 153, 255), 'd');
        setName("Dagger");
        setTileName("Dagger");
        setDescription("An iron dagger. Sharp, but a bit finicky.");
        setMinDamage(1);
        setMaxDamage(9);
        setWeight(3);
        setAccuracy(-3);
        setDamageType(DamageType.PIERCING);
    }


    @Override
    public boolean collides(Space space) {
        return space.isOccupied();
    }

    @Override
    public void onLand(Space space) {
        space.addItem(this);
    }

    @Override
    public void onCollision(Space beforeSpace, Space collidingSpace) {
        collidingSpace.getOccupant().dealDamage(this.generateDamage(), this.getDamageType());
        onLand(beforeSpace);
    }
}
