package game.gamelogic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import game.floorgeneration.Shopper;
import game.floorgeneration.pools.Pool;
import game.gameobjects.items.Item;

public interface HasDrops {
    public Pool<Supplier<Item>> getItemPool();
    public int getDropPoints();
    default List<Item> generateDrops(){
        List<Item> drops = new ArrayList<Item>();
        Shopper<Item> shopper = new Shopper<>(getDropPoints(),getItemPool());
        while (shopper.hasPoints()){
            Item generated = shopper.generate();
            if (generated != null)
                drops.add(generated);
        }
        return drops;
    }
}
