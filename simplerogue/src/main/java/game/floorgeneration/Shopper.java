package game.floorgeneration;

import java.util.function.Supplier;

public class Shopper<T> {
    private int points;
    private Pool<Supplier<T>> pool;

    public Shopper(int points, Pool<Supplier<T>> pool) {
        this.points = points;
        this.pool = pool;
    }

    public T generate(){
        Supplier<T> supplier = pool.getRandom(points);
        if (supplier != null) {
            points -= pool.getPrice(supplier);
            return supplier.get();
        }
        return null;
    }

    public boolean hasPoints(){
        return points >= 0 && points >= pool.getLowestPrice();
    }

}
