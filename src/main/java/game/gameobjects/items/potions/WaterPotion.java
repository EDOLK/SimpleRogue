package game.gameobjects.items.potions;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Aimable;
import game.gameobjects.Space;
import game.gameobjects.items.Item;
import game.gameobjects.statuses.Wet;
import game.gameobjects.terrains.liquids.Water;

public class WaterPotion extends Item implements Aimable{
    
    private Water water;

    public WaterPotion(){
        setName("Water Potion");
        setTileName("Blue Potion");
        setDescription("A bottle of water");
        setCharacter('p');
        setFgColor(TileColor.create(100, 100, 255, 255));
        setBgColor(TileColor.transparent());
        setWeight(1);
    }

    @Override
    public boolean collides(Space space) {
        return space.isOccupied();
    }

    @Override
    public void onLand(Space space) {
        this.water = new Water(10);
        space.addTerrain(this.water);
    }

    @Override
    public void onCollision(Space beforeSpace, Space collidingSpace) {
        onLand(collidingSpace);
        if (this.water != null) {
            collidingSpace.getOccupant().addStatus(new Wet(this.water));
        }
    }

}
