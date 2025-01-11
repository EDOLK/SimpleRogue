package game.gamelogic;

import java.util.ArrayList;
import java.util.List;

import game.floorgeneration.ItemFactory;
import game.floorgeneration.Pool;
import game.floorgeneration.ItemFactory.ItemIdentifier;
import game.gameobjects.items.Item;

public interface HasDrops {
    public Pool<ItemIdentifier> getItemPool();
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
            ItemIdentifier identifier = getItemPool().getRandom(t);
            drops.add(ItemFactory.createItem(identifier));
            t -= getItemPool().getPrice(identifier);
        }
        return drops;
    }
}
