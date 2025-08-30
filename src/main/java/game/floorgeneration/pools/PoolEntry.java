package game.floorgeneration.pools;

import java.util.function.Supplier;

public class PoolEntry<T> {

    private Supplier<T> t = null;
    private int price = 0;
    private int amount = -1;
    private double weight = 1.0d;

    private PoolEntry(){}

    public T get() {
        return t.get();
    }

    public int getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public double getWeight() {
        return weight;
    }

    void decrementAmount(){
        amount--;
    }

    PoolEntry<T> copy(){
        return new Builder<T>()
            .with(t)
            .withPrice(price)
            .withAmount(amount)
            .withWeight(weight)
            .build();
    }

    public static class Builder<T> {
        private PoolEntry<T> entry = new PoolEntry<T>();
        public Builder<T> with(Supplier<T> t){
            entry.t = t;
            return this;
        }
        public Builder<T> withPrice(int price){
            entry.price = price;
            return this;
        }
        public Builder<T> withAmount(int amount){
            entry.amount = amount;
            return this;
        }
        public Builder<T> withWeight(double weight){
            entry.weight = weight;
            return this;
        }
        public PoolEntry<T> build(){
            return this.entry;
        }
    }

}
