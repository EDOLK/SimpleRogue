package game.gameobjects.items.armor;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.resistances.RangeResistance;
import game.gameobjects.DamageType;

public class LeatherGloves extends Armor {

    public LeatherGloves(){
        super(TileColor.transparent(), TileColor.create(168, 73, 0, 255), 'g');
        setName("Leather Gloves");
        setTileName("Leather Gloves");
        setDescription("Gloves made of tanned leather.");
        getResistances().add(new RangeResistance(DamageType.PIERCING, this, 0, 1));
        getResistances().add(new RangeResistance(DamageType.SLASHING, this, 0, 1));
        getResistances().add(new RangeResistance(DamageType.BLUNT, this, 0, 1));
        setArmorType(ArmorType.HANDS);
        setWeight(1);
    }
    
}
