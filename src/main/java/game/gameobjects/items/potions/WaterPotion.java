package game.gameobjects.items.potions;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Aimable;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;
import game.gameobjects.statuses.Wet;
import game.gameobjects.terrains.liquids.Water;

public class WaterPotion extends Item implements Aimable{
    
    public WaterPotion(){
        setName("Water Potion");
        setTileName("Blue Potion");
        setDescription("A bottle of water");
        setCharacter('p');
        setfGColor(TileColor.create(100, 100, 255, 255));
        setbGColor(TileColor.transparent());
        setWeight(1);
    }

    @Override
    public void onHit(Entity target) {
        target.addStatus(new Wet(new Water(1)));
    }

    @Override
    public void onLand(Space space) {
        space.addTerrain(new Water(10));
    }

    @Override
    public boolean landsOnHit() {
        return true;
    }
    
}
