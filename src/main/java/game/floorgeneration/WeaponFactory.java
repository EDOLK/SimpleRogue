package game.floorgeneration;

import game.gamelogic.Upgradable;
import game.gamelogic.Upgrader;
import game.gameobjects.enchantments.WeaponEnchantment;
import game.gameobjects.items.weapons.Weapon;

public class WeaponFactory extends ItemFactory{

    private Weapon weapon;

    public WeaponFactory(Weapon weapon){
        this.weapon = weapon;
    }

    public WeaponFactory withLevel(int level){
        Upgrader upgrader = new Upgrader() {

            @Override
            public boolean doUpgrade(Upgradable upgradable) {
                weapon.setLevel(weapon.getLevel() + level);
                return true;
            }

        };
        weapon.upgrade(upgrader);
        return this;
    }
    
    public WeaponFactory withEnchantment(WeaponEnchantment enchantment){
        weapon.setEnchantment(enchantment);
        return this;
    }
    
    @Override
    public Weapon create() {
        return weapon;
    }

}
