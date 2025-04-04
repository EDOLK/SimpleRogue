package game.gameobjects.entities;

import static game.App.randomNumber;

import java.util.function.Supplier;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.floorgeneration.Pool;
import game.gamelogic.DropsXP;
import game.gamelogic.HasDodge;
import game.gamelogic.HasDrops;
import game.gamelogic.behavior.AnimalBehavior;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHit;
import game.gameobjects.DamageType;
import game.gameobjects.items.Corpse;
import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Poisoned;

public class Snake extends Animal implements DropsXP, HasDodge, OnHit, HasDrops{
    
    private int dropPoints = randomNumber(3, 7);

    public Snake() {
        super(TileColor.transparent(), TileColor.create(38, 125, 34, 255), 's');
        setMaxHP(5);
        setHP(5);
        setWeight(5);
        setName("Giant Snake");
        setTileName("Giant Snake");
        setDescription("A giant Snake. Fast and venomous.");
        setCorpse(new Corpse(this));
        setBehavior(new AnimalBehavior(this));

        Weapon fangs = new Weapon();
        fangs.setName("fangs");
        fangs.setDamageType(DamageType.PIERCING);
        fangs.setDamage(1, 3);
        setUnarmedWeapon(fangs);
    }

    @Override
    public int dropXP() {
        return 10;
    }

    @Override
    public int getDodge() {
        return 7;
    }

    @Override
    public Pool<Supplier<Item>> getItemPool() {
        return Dungeon.getCurrentDropPool();
    }

    @Override
    public int getDropPoints() {
        return dropPoints;
    }

    @Override
    public void doOnHit(Entity self, Entity other, AttackInfo attackInfo) {
        other.addStatus(new Poisoned(1, 3, 3));
    }

}
