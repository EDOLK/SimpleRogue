package game.gameobjects.items.armor;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.resistances.RangeResistance;
import game.gameobjects.DamageType;

public class Chainmail extends Armor {

    public Chainmail(){
        super(TileColor.transparent(), TileColor.create(153, 153, 153, 255), 'c');
        setName("Chainmail");
        setDescription("Armor made of interlocking metal chains. Effectively catches bladed weapons, though a bit heavy.");
        getResistances().add(new RangeResistance(DamageType.PIERCING, this, 2, 6));
        getResistances().add(new RangeResistance(DamageType.SLASHING, this, 2, 6));
        setDodge(-3);
        setArmorType(ArmorType.CHEST_INNER);
        setWeight(10);
    }

}
