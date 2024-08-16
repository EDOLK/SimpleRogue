package game.gameobjects.items.weapons;

import org.hexworks.zircon.api.color.TileColor;

import game.gameobjects.DamageType;

public class HandAxe extends Weapon{
    public HandAxe(){
        super(TileColor.transparent(), TileColor.create(153, 153, 153, 255), 'a');
        setName("Hand Axe");
        setTileName("Axe");
        setDescription("A Hand axe. Reliable.");
        setMinDamage(3);
        setMaxDamage(4);
        setWeight(5);
        setDamageType(DamageType.SLASHING);
    }
}
