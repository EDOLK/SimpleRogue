package game.gameobjects.items.armor;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.resistances.RangeResistance;
import game.gameobjects.DamageType;

public class LeatherArmor extends Armor{
    
    public LeatherArmor(){
        super(TileColor.transparent(), TileColor.create(168, 73, 0, 255), 'a');
        setName("Leather Armor");
        setTileName("Leather Armor");
        setDescription("Armor made of tanned leather. Tough and versitile.");
        getResistances().add(new RangeResistance(DamageType.PIERCING, this, 1, 3));
        getResistances().add(new RangeResistance(DamageType.SLASHING, this, 1, 3));
        getResistances().add(new RangeResistance(DamageType.BLUNT, this, 1, 3));
        setArmorType(ArmorType.CHEST_INNER);
        setWeight(5);
    }

    
}
