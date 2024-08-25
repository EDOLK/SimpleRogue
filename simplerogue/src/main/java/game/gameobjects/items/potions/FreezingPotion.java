package game.gameobjects.items.potions;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Aimable;
import game.gamelogic.Consumable;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;
import game.gameobjects.terrains.gasses.FreezingAir;

public class FreezingPotion extends Item implements Aimable, Consumable {
    
    public FreezingPotion(){
        setTileName("Cyan Potion");
        setCharacter('p');
        setfGColor(TileColor.create(100, 100, 255, 255));
        setName("Freezing potion");
        setTileName("Cyan Potion");
        setWeight(1);
        setDescription("A potion filled with a cloudy, bluish liquid. It's quite cold to the touch");
    }

    @Override
    public boolean consume(Entity consumer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'consume'");
    }

    @Override
    public void onHit(Entity target) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onHit'");
    }

    @Override
    public void onLand(Space space) {
        space.addGas(new FreezingAir(10));
    }

    @Override
    public boolean landsOnHit() {
        return true;
    }

}
