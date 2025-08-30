package game.gameobjects.entities.props;

import java.util.function.Supplier;

import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.Dungeon;
import game.floorgeneration.pools.Pool;
import game.gamelogic.HasDrops;
import game.gameobjects.items.Item;

public class Crate extends ContainerProp{

    public Crate(){
        super(TileColor.transparent(), TileColor.create(153, 57, 2, 255), 'C');
        setBaseMaxHP(5);
        setHP(5);
        setWeight(5);
        setName("Crate");
        setTileName("Crate");
        setDescription("A crate. It probably has things in it.");
        for (Item item : new HasDrops() {

            @Override
            public Pool<Item> getItemPool() {
                return Dungeon.getCurrentPropDropPool();
            }

            @Override
            public int getDropPoints() {
                return Math.random() >= 0.6 ? App.randomNumber(2,6) : 0;
            }
            
        }.generateDrops()) {
            addItemToInventory(item);
        }
    }
}
