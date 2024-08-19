package game.gameobjects.entities;

import static game.App.randomNumber;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.floorgeneration.Pool;
import game.floorgeneration.ItemFactory.ItemIdentifier;
import game.gamelogic.DropsXP;
import game.gamelogic.HasDodge;
import game.gamelogic.HasDrops;
import game.gameobjects.DamageType;
import game.gameobjects.items.Corpse;
import game.gameobjects.items.weapons.Weapon;

public class Rat extends Animal implements DropsXP, HasDodge, HasDrops{
    
    private int dropPoints = randomNumber(3, 10);

    public Rat() {

        super(TileColor.transparent(), TileColor.create(125, 76, 36, 255), 'r');
        setMaxHP(10);
        setHP(10);
        setName("Giant Rat");
        setTileName("Giant Rat");
        setDescription("A giant rat, made feral by the negative influence of the dungeon.");
        setWeight(5);
        setCorpse(new Corpse(this));

        Weapon teeth = new Weapon();
        teeth.setName("teeth");
        teeth.setDamageType(DamageType.PIERCING);
        teeth.setDamage(1, 3);
        setUnarmedWeapon(teeth);
    }

    @Override
    public int dropXP() {
        return 5;
    }

    @Override
    public int getDodge() {
        return 5;
    }

    @Override
    public boolean isActive() {
        return getHP() > 0;
    }

    @Override
    public Pool<ItemIdentifier> getItemPool() {
        return Dungeon.getCurrentDropPool();
    }

    @Override
    public int getDropPoints() {
        return dropPoints;
    }

    @Override
    public void setDropPoints(int points) {
        dropPoints = points;
    }

}
