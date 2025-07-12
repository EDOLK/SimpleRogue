package game.gameobjects.entities.props;

import java.util.function.Supplier;

import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.Dungeon;
import game.floorgeneration.pools.Pool;
import game.gamelogic.HasDrops;
import game.gameobjects.items.Item;

public class Barrel extends ContainerProp{

    public Barrel(){
        super(TileColor.transparent(), TileColor.create(153, 57, 2, 255), 'B');
        setBaseMaxHP(5);
        setHP(5);
        setWeight(5);
        setName("Barrel");
        setTileName("Barrel");
        setDescription("A barrel. It probably has things in it.");
        for (Item item : new HasDrops() {

            @Override
            public Pool<Supplier<Item>> getItemPool() {
                return Dungeon.getCurrentPropDropPool();
            }

            @Override
            public int getDropPoints() {
                return Math.random() >= 0.6 ? App.randomNumber(2,5) : 0;
            }
            
        }.generateDrops()) {
            addItemToInventory(item);
        }
    }

}
