package game.floorgeneration.pools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Pool<T> {

    private Map<T, Integer> map;
    private int lowestPrice;

    public Pool(Map<T, Integer> map){
        this.map = map;
    }

    public Pool() {
        super();
    }

    public Map<T, Integer> getMap() {
        return map;
    }

    public void setMap(Map<T, Integer> map){
        this.map = map;
        lowestPrice = calculateLowestPrice();
    }
    
    public T getRandom(int priceLimit){
        List<Entry<T, Integer>> entries = new ArrayList<Entry<T,Integer>>(map.entrySet());
        Collections.shuffle(entries);
        for (Entry<T,Integer> entry : entries) {
            if (entry.getValue() <= priceLimit){
                return entry.getKey();
            }
        }
        return null;
    }
    
    public int getLowestPrice(){
        return lowestPrice;
    }
    
    public int getPrice(T t){
        return map.get(t);
    }

    private int calculateLowestPrice(){
        Integer lowest = Integer.MAX_VALUE;
        for (Integer integer : map.values()) {
            if (integer < lowest){
                lowest = integer;
            }
        }
        return lowest;
    }
    
}
