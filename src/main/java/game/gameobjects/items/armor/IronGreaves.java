package game.gameobjects.items.armor;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.resistances.RangeResistance;
import game.gameobjects.DamageType;

public class IronGreaves extends Armor {

    public IronGreaves(){
        super(TileColor.transparent(), TileColor.create(200, 200, 200, 255), 'g');
        setName("Iron Greaves");
        setDescription("Iron greaves.");
        getResistances().add(new RangeResistance(DamageType.PIERCING, this, 1, 3));
        getResistances().add(new RangeResistance(DamageType.SLASHING, this, 1, 3));
        setDodge(-2);
        setWeight(5);
        setArmorType(ArmorType.LEGS);
    }
}
