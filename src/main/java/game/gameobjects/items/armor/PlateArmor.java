package game.gameobjects.items.armor;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.resistances.RangeResistance;
import game.gameobjects.DamageType;

public class PlateArmor extends Armor{

    public PlateArmor(){
        super(TileColor.transparent(), TileColor.create(200, 200, 200, 255), 'p');
        setName("Plate Armor");
        setTileName("Chestplate");
        setDescription("Massive plate armor. Staggeringly heavy.");
        resistances.add(new RangeResistance(DamageType.PIERCING, 2, 6, this, 1, 3));
        resistances.add(new RangeResistance(DamageType.SLASHING, 2, 6, this, 1, 3));
        resistances.add(new RangeResistance(DamageType.BLUNT, 1, 3, this, 1, 3));
        setDodge(-7);
        setArmorType(ArmorType.CHEST_OUTER);
        setWeight(20);
    }
    
}
