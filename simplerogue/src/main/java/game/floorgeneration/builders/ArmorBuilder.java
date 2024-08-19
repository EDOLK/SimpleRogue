package game.floorgeneration.builders;

import game.gameobjects.enchantments.ArmorEnchantment;
import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;

public class ArmorBuilder extends ItemBuilder{

    protected ArmorBuilder(Class<? extends Item> tClass, Object[] args) {
        super(tClass, args);
    }

    protected ArmorBuilder(Item item){
        super(item);
    }

    public ArmorBuilder withEnchantment(ArmorEnchantment armorEnchantment){
        ((Armor)t).setEnchantment(armorEnchantment);
        return this;
    }

    @Override
    public Armor build(){
        return (Armor)t;
    }
    
}
