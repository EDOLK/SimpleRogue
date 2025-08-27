package game.floorgeneration;

import java.util.function.Supplier;

import game.floorgeneration.pools.PoolEntry;
import game.floorgeneration.pools.Pool;

public class Shopper<T> {
    private int points;
    private Pool<Supplier<T>> pool;

    public Shopper(int points, Pool<Supplier<T>> pool) {
        this.points = points;
        this.pool = pool;
    }

    public T generate(){
        PoolEntry<Supplier<T>> entry = pool.getRandom(points);
        Supplier<T> supplier = entry.get();
        if (supplier != null) {
            points -= entry.getPrice();
            return supplier.get();
        }
        return null;
    }

    public boolean hasPoints(){
        return points >= 0 && points >= pool.getLowestPrice();
    }

}
