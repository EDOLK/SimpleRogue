package game.floorgeneration;

import game.gamelogic.Upgrader;
import game.gameobjects.enchantments.ArmorEnchantment;
import game.gameobjects.items.armor.Armor;

public class ArmorFactory extends ItemFactory{

    private Armor armor;

    public ArmorFactory(Armor armor){
        this.armor = armor;
    }
    
    public ArmorFactory withLevel(int level){
        Upgrader upgrader = new Upgrader() {
            @Override
            public int getUpgradeValue() {
                return level;
            }
        };
        armor.upgrade(upgrader);
        return this;
    }
    
    public ArmorFactory withEnchantment(ArmorEnchantment enchantment){
        armor.setEnchantment(enchantment);
        return this;
    }

    @Override
    public Armor create() {
        return armor;
    }
}
