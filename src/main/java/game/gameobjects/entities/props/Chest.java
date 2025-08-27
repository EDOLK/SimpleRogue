package game.gameobjects.entities.props;

import java.util.function.Supplier;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.floorgeneration.pools.Pool;
import game.gamelogic.HasDrops;
import game.gameobjects.items.Item;

public class Chest extends ContainerProp{

    public Chest(){
        super(TileColor.transparent(), TileColor.create(230, 188, 64, 255), 'c');
        setBaseMaxHP(5);
        setHP(5);
        setWeight(5);
        setName("Chest");
        setTileName("Chest");
        setDescription("A chest. It probably has things in it.");
        for (Item item : new HasDrops() {

            @Override
            public Pool<Supplier<Item>> getItemPool() {
                return Dungeon.getCurrentTreasurePool();
            }

            @Override
            public int getDropPoints() {
                return Dungeon.getCurrentDepth() * 5;
            }
            
        }.generateDrops()) {
            addItemToInventory(item);
        }
    }
}
