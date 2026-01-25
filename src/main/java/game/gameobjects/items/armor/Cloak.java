package game.gameobjects.items.armor;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.RangeResistance;
import game.gameobjects.DamageType;

public class Cloak extends Armor{
    
    public Cloak(){
        super(TileColor.transparent(), TileColor.create(168, 73, 0, 255), 'c');
        setName("Cloak");
        setTileName("Cloak");
        setDescription("A simple linen cloak. Lightweight, though it doesn't offer much protection.");
        resistances.add(new RangeResistance(DamageType.FROST, 1, 3, this, 1, 3));
        resistances.add(new RangeResistance(DamageType.FIRE, 0, 3, this, 1, 3));
        resistances.add(new PercentageResistance(DamageType.FIRE, 0.50, this, 0.50));
        setArmorType(ArmorType.CHEST_OUTER);
        setWeight(1);
    }
}
