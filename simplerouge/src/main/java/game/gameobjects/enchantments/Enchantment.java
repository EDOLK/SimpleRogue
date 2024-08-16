package game.gameobjects.enchantments;

public abstract class Enchantment<T>{

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
