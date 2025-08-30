package game.floorgeneration;

import game.floorgeneration.pools.PoolEntry;
import game.floorgeneration.pools.Pool;

public class Shopper<T> {
    private int points;
    private Pool<T> pool;

    public Shopper(int points, Pool<T> pool) {
        this.points = points;
        this.pool = pool;
    }

    public T generate(){
        PoolEntry<T> entry = pool.getRandom(points);
        if (entry != null) {
            points -= entry.getPrice();
            return entry.get();
        }
        return null;
    }

    public boolean hasPoints(){
        return points >= 0 && points >= pool.getLowestPrice();
    }

}
