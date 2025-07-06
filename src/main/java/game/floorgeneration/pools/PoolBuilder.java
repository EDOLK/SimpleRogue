package game.floorgeneration.pools;

import java.util.HashMap;
import java.util.Map;

public class PoolBuilder<T> {
    private Map<T, Integer> map = new HashMap<>();
    public PoolBuilder() {
        super();
    }
    public PoolBuilder<T> put(T key, Integer value){
        this.map.put(key, value);
        return this;
    }
    public Pool<T> build(){
        return new Pool<T>(this.map);
    }
}
