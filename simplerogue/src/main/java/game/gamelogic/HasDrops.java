package game.gamelogic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import game.floorgeneration.Pool;
import game.gameobjects.items.Item;

public interface HasDrops {
    public Pool<Supplier<Item>> getItemPool();
    public int getDropPoints();
    public void setDropPoints(int points);
    default void addDropPoints(int points){
        setDropPoints(getDropPoints() + points);
    }
    default void subtractDropPoints(int points){
        setDropPoints(getDropPoints() - points);
    }
    default List<Item> generateDrops(){

        List<Item> drops = new ArrayList<Item>();

        int t = getDropPoints();

        while (t >= getItemPool().getLowestPrice()) {
            Supplier<Item> itemSupplier = getItemPool().getRandom(t);
            drops.add(itemSupplier.get());
            t -= getItemPool().getPrice(itemSupplier);
        }
        return drops;
    }
}
