package game.floorgeneration.pools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.Collectors;

public class ConcretePool<T> implements Pool<T> {

    private List<PoolEntry<T>> entries = new ArrayList<>();

    private ConcretePool(){}

    @Override
    public PoolEntry<T> getRandom(int priceLimitMin, int priceLimitMax){
        List<PoolEntry<T>> filteredEntries = entries.stream()
            .filter((e) -> e.getPrice() >= priceLimitMin && e.getPrice() <= priceLimitMax)
            .filter((e) -> e.getAmount() > 0 || e.getAmount() == -1)
            .collect(Collectors.toList());
        Collections.shuffle(filteredEntries);
        double rnd = new Random().nextDouble(filteredEntries.stream().mapToDouble((e) -> e.getWeight()).sum());
        for (PoolEntry<T> poolEntry : filteredEntries) {
            if (rnd < poolEntry.getWeight()) {
                if (poolEntry.getAmount() != -1)
                    poolEntry.decrementAmount();
                return poolEntry;
            }
            rnd -= poolEntry.getWeight();
        }
        return null;
    }
    
    @Override
    public int getLowestPrice(){
        OptionalInt lowest = entries.stream().mapToInt((e)->e.getPrice()).min();
        if (lowest.isPresent()) {
            return lowest.getAsInt();
        }
        return 0;
    }

    @Override
    public int getHighestPrice() {
        OptionalInt highest = entries.stream().mapToInt((e)->e.getPrice()).max();
        if (highest.isPresent()) {
            return highest.getAsInt();
        }
        return 0;
    }

    @Override
    public Pool<T> copy() {
        return new Builder<T>()
            .putAll(
                entries.stream()
                    .map((e) -> e.copy())
                    .collect(Collectors.toList())
            )
            .build();
    }

    public static class Builder<T> {
        private ConcretePool<T> pool = new ConcretePool<T>();
        public Builder() {
            super();
        }
        public Builder<T> put(PoolEntry<T> entry){
            if (!pool.entries.contains(entry))
                pool.entries.add(entry);
            return this;
        }
        public Builder<T> putAll(List<PoolEntry<T>> entries){
            entries.forEach(this::put);
            return this;
        }
        public ConcretePool<T> build(){
            return this.pool;
        }
    }

}
