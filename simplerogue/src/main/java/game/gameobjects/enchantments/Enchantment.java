package game.gameobjects.enchantments;

import game.gamelogic.Examinable;
import game.gameobjects.DisplayableTile;

public abstract class Enchantment<T> extends DisplayableTile implements Examinable{

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

    @Override
    public String getName() {
        return hasPrefix() ? getPrefix() : hasSuffix() ? getSuffix() : "Enchantment";
    }

}
