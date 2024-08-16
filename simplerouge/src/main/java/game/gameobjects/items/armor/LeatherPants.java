package game.gameobjects.items.armor;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.resistances.RangeResistance;
import game.gameobjects.DamageType;

public class LeatherPants extends Armor {
    
    public LeatherPants(){
        super(TileColor.transparent(), TileColor.create(168, 73, 0, 255), 'p');
        setName("Leather Pants");
        setDescription("Pants made of tanned leather.");
        getResistances().add(new RangeResistance(DamageType.PIERCING, this, 0, 2));
        getResistances().add(new RangeResistance(DamageType.SLASHING, this, 0, 2));
        getResistances().add(new RangeResistance(DamageType.BLUNT, this, 1, 2));
        setArmorType(ArmorType.LEGS);
        setWeight(2);
    }
}
