package game.gameobjects.items.armor;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.resistances.RangeResistance;
import game.gameobjects.DamageType;

public class LeatherCap extends Armor{
    public LeatherCap(){
        super(TileColor.transparent(), TileColor.create(168, 73, 0, 255), 'c');
        setName("Leather Cap");
        setTileName("Leather Cap");
        setDescription("A simple leather cap.");
        getResistances().add(new RangeResistance(DamageType.PIERCING, this, 0, 2));
        getResistances().add(new RangeResistance(DamageType.SLASHING, this, 0, 2));
        getResistances().add(new RangeResistance(DamageType.BLUNT, this, 0, 2));
        setArmorType(ArmorType.HEAD);
        setWeight(1);
    }
}
