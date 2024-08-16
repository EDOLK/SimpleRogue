package game.gameobjects.items.armor;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.resistances.RangeResistance;
import game.gameobjects.DamageType;

public class IronHelm extends Armor{

    public IronHelm(){
        super(TileColor.transparent(), TileColor.create(200, 200, 200, 255), 'h');
        setName("Iron Helm");
        setDescription("A sturdy iron helmet.");
        getResistances().add(new RangeResistance(DamageType.PIERCING, this, 1, 3));
        getResistances().add(new RangeResistance(DamageType.SLASHING, this, 1, 3));
        setArmorType(ArmorType.HEAD);
        setWeight(3);
        setDodge(-1);
    }
}
