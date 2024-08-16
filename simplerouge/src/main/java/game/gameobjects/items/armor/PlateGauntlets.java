package game.gameobjects.items.armor;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.resistances.RangeResistance;
import game.gameobjects.DamageType;

public class PlateGauntlets extends Armor {
    
    public PlateGauntlets(){
        super(TileColor.transparent(), TileColor.create(200, 200, 200, 255), 'g');
        setName("Plate Gauntlets");
        setDescription("Plate Gauntlets. Decent protection, though a bit heavy.");
        getResistances().add(new RangeResistance(DamageType.PIERCING, this, 1, 2));
        getResistances().add(new RangeResistance(DamageType.SLASHING, this, 1, 2));
        setArmorType(ArmorType.HANDS);
        setDodge(-1);
        setWeight(5);
    }
}
