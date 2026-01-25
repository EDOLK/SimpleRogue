package game.gameobjects.items.armor;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.resistances.RangeResistance;
import game.gameobjects.DamageType;

public class LeatherPants extends Armor {
    
    public LeatherPants(){
        super(TileColor.transparent(), TileColor.create(168, 73, 0, 255), 'p');
        setName("Leather Pants");
        setTileName("Leather Pants");
        setDescription("Pants made of tanned leather.");
        resistances.add(new RangeResistance(DamageType.PIERCING, 0, 2, this, 1, 2));
        resistances.add(new RangeResistance(DamageType.SLASHING, 0, 2, this, 1, 2));
        resistances.add(new RangeResistance(DamageType.BLUNT, 1, 2, this, 1, 2));
        setArmorType(ArmorType.LEGS);
        setWeight(2);
    }
}
