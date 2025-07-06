package game.gameobjects.entities;

import java.util.ArrayList;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.HasAccuracy;
import game.gamelogic.HasResistances;
import game.gamelogic.resistances.RangeResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.items.weapons.Weapon;

public class DireRat extends Rat implements HasResistances, HasAccuracy{

    private ArrayList<Resistance> resistances = new ArrayList<>();

    public DireRat(){
        super();
        setName("Giant Dire Rat");
        setTileName("Dire Rat");
        setDescription("A Giant Dire Rat, with sleek white fur. Its fur is thick enough to give it some protection against edged weapons.");
        setfGColor(TileColor.create(200, 200, 200, 255));
        resistances.add(new RangeResistance(DamageType.SLASHING, 0, 3));
        resistances.add(new RangeResistance(DamageType.PIERCING, 0, 3));

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
    public ArrayList<Resistance> getResistances() {
        return resistances;
    }

    @Override
    public int getAccuracy() {
        return 1;
    }
    
}
