package game.gameobjects.items.weapons;

import static game.App.randomNumber;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Aimable;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;

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
    public void onHit(Entity target) {
        target.dealDamage(randomNumber(getMinDamage(), getMaxDamage()), getDamageType());
    }

    @Override
    public void onLand(Space space) {
        space.addItem(this);
    }

    @Override
    public boolean landsOnHit() {
        return true;
    }
}
