package game.floorgeneration.builders;

import game.gameobjects.enchantments.WeaponEnchantment;
import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;

public class WeaponBuilder extends ItemBuilder{
    
    protected WeaponBuilder(Class<? extends Item> tClass, Object[] args) {
        super(tClass, args);
    }

    protected WeaponBuilder(Item item){
        super(item);
    }
    
    public WeaponBuilder withEnchantment(WeaponEnchantment weaponEnchantment){
        ((Weapon)t).setEnchantment(weaponEnchantment);
        return this;
    }

    @Override
    public Weapon build() {
        return (Weapon)t;
    }

}
