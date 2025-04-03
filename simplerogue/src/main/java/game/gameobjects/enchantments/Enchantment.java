package game.gameobjects.enchantments;

import game.gamelogic.Examinable;

public abstract class Enchantment<T> implements Examinable{

    protected String prefix;
    protected String suffix;

    public boolean hasPrefix(){
        return getPrefix() != null;
    }
    
    public boolean hasSuffix(){
        return getSuffix() != null;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

}
