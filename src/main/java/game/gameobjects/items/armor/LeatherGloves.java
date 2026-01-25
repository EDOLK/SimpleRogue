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
        resistances.add(new RangeResistance(DamageType.PIERCING, 0, 1, this, 1, 1));
        resistances.add(new RangeResistance(DamageType.SLASHING, 0, 1, this, 1, 1));
        resistances.add(new RangeResistance(DamageType.BLUNT, 0, 1, this, 1, 1));
        setArmorType(ArmorType.HANDS);
        setWeight(1);
    }
    
}
