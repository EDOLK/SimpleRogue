package game.gamelogic;

import java.util.HashMap;
import java.util.Map;

public class AttributeMap {

    private Map<Attribute,Integer> aMap = new HashMap<>();

    public AttributeMap() {
        for (Attribute attribute : Attribute.values()) {
            aMap.put(attribute,0);
        }
    }

    public AttributeMap(Map<Attribute,Integer> aMap){
        this.aMap = aMap;
    }

    public int get(Attribute attribute){
        return aMap.get(attribute);
    }

    public void increment(Attribute attribute){
        aMap.compute(attribute, (a,v) -> {
            return (v+1);
        });
    }

    public void decrement(Attribute attribute){
        aMap.compute(attribute, (a,v) -> {
            return (v-1);
        });
    }

    public void set(Attribute attribute, int amount){
        aMap.put(attribute, amount);
    }

    public enum Attribute {
        STRENGTH("STR"),
        DEXTERITY("DEX"),
        ENDURANCE("END"),
        INTELLIGENCE("INT"),
        CHARISMA("CHA"),
        WISDOM("WIS"),
        LUCK("LCK");

        public final String shortHand;

        private Attribute(String shortHand){
            this.shortHand = shortHand;
        }

    }
}
