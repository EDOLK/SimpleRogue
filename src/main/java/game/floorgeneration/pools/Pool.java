package game.floorgeneration.pools;

public interface Pool<T> {
    default PoolEntry<T> getRandom(int priceLimitMax){
        return getRandom(0, priceLimitMax);
    };
    public PoolEntry<T> getRandom(int priceLimitMin, int priceLimitMax);
    public int getLowestPrice();
    public Pool<T> copy();
}
