package game.gameobjects.entities;

import static game.App.randomNumber;

import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.HasAccuracy;
import game.gamelogic.HasResistances;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHit;
import game.gamelogic.resistances.RangeResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Bleeding;

public class DireRat extends Rat implements HasResistances, HasAccuracy, OnHit{

    public DireRat(){
        super();
        setName("Giant Dire Rat");
        setTileName("Dire Rat");
        setDescription("A Giant Dire Rat, with sleek white fur. Its fur is thick enough to give it some protection against edged weapons.");
        setFgColor(TileColor.create(200, 200, 200, 255));

        Weapon teeth = new Weapon();
        teeth.setName("teeth");
        teeth.setDamageType(DamageType.PIERCING);
        teeth.setDamage(1, 4);
        setUnarmedWeapon(teeth);
    }

    @Override
    public int dropXP() {
        return 7;
    }

    @Override
    public List<Resistance> getResistances() {
        return List.of(
            new RangeResistance(DamageType.SLASHING, 0, 3),
            new RangeResistance(DamageType.PIERCING, 0, 3)
        );
    }

    @Override
    public int getAccuracy() {
        return 4;
    }

    @Override
    public int getDropPoints() {
        return 5 + super.getDropPoints();
    }

    @Override
    public void doOnHit(Entity self, Entity other, AttackInfo attackInfo) {
        if (randomNumber(1, 4) == 4) {
            other.addStatus(new Bleeding(randomNumber(1, 5), 0, 1));
        }
    }
    
}
