package game.gameobjects.items.weapons;

import org.hexworks.zircon.api.color.TileColor;

import game.gameobjects.DamageType;

public class ShortSword extends Weapon {
    
    public ShortSword(){
        super(TileColor.transparent(), TileColor.create(153, 153, 153, 255), 's');
        setName("Shortsword");
        setTileName("Shortsword");
        setDescription("An iron shortsword.");
        setMinDamage(1);
        setMaxDamage(6);
        setWeight(5);
        setDamageType(DamageType.SLASHING);
    }
}
