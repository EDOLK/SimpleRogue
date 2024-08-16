package game.floorgeneration.builders;

import game.gamelogic.Levelable;
import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;

public class ItemBuilder extends Builder<Item>{

    public static ItemBuilder newBuilder(Class<? extends Item> clazz, Object... args){
        if (clazz.equals(Weapon.class)){
            return new WeaponBuilder(clazz, args);
        }
        if (clazz.equals(Armor.class)){
            return new ArmorBuilder(clazz, args);
        }
        return new ItemBuilder(clazz, args);
    }
    
    public static ItemBuilder newBuilder(Item item){
        if (item instanceof Weapon){
            return new WeaponBuilder(item);
        }
        if (item instanceof Armor){
            return new ArmorBuilder(item);
        }
        return new ItemBuilder(item);
    }

    protected ItemBuilder(Class<? extends Item> tClass, Object... args) {
        super(tClass, args);
    }
    
    protected ItemBuilder(Item item){
        super(item);
    }
    
    public ItemBuilder withLevel(int level){
        if (t instanceof Levelable levelable){
            levelable.setLevel(level);
        }
        return this;
    }
    
}
