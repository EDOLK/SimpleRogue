package game.gameobjects.items.armor;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.resistances.RangeResistance;
import game.gameobjects.DamageType;

public class Cloak extends Armor{
    
    public Cloak(){
        super(TileColor.transparent(), TileColor.create(168, 73, 0, 255), 'c');
        setName("Cloak");
        setTileName("Cloak");
        setDescription("A simple linen cloak. Lightweight, though it doesn't offer much protection.");
        getResistances().add(new RangeResistance(DamageType.FROST, this, 1, 3));
        getResistances().add(new RangeResistance(DamageType.FIRE, this, 0, 3));
        setArmorType(ArmorType.CHEST_OUTER);
        setWeight(1);
    }
}
