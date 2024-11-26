package game.gameobjects.items.weapons;

import static game.App.randomNumber;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.HasAccuracy;
import game.gamelogic.Levelable;
import game.gamelogic.Upgradable;
import game.gamelogic.Upgrader;
import game.gameobjects.DamageType;
import game.gameobjects.enchantments.WeaponEnchantment;
import game.gameobjects.items.Item;
import game.gameobjects.items.scrolls.ScrollOfEnchantment;
import game.gameobjects.items.scrolls.ScrollOfUpgrade;


public class Weapon extends Item implements HasAccuracy, Levelable, Upgradable{
    
    private DamageType damageType;
    private int minDamage = 0;
    private int maxDamage = 0;
    private int accuracy = 0;
    private int level = 1;
    private WeaponEnchantment enchantment;

    public Weapon(TileColor bGColor, TileColor fGColor, char character) {
        super(bGColor, fGColor, character);
    }

    public Weapon(){
        super();
    }

    public WeaponEnchantment getEnchantment() {
        return enchantment;
    }

    public void setEnchantment(WeaponEnchantment enchantment) {
        this.enchantment = enchantment;
    }
    
    public void setDamage(int minDamage, int maxDamage){
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public void setMinDamage(int minDamage) {
        this.minDamage = minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }
    
    public int generateDamage(){
        return randomNumber(minDamage, maxDamage) * level;
    }

    @Override
    public int getAccuracy() {
        return accuracy;
    }

    @Override
    public int getLevel() {
        return level;
    }
    
    public void setAccuracy(int accuracy){
        this.accuracy = accuracy;
    }

    @Override
    public boolean upgrade(Upgrader upgrader) {
        return upgrader.doUpgrade(this);
    }

    @Override
    public boolean setLevel(int level) {
        this.level = level;
        return true;
    }

    @Override
    public boolean canUpgrade(Upgrader upgrader) {
        return upgrader instanceof ScrollOfUpgrade || upgrader instanceof ScrollOfEnchantment;
    }

    @Override
    public String getName() {
        String n = super.getName();
        WeaponEnchantment weaponEnchantment = getEnchantment();
        if (weaponEnchantment != null){
            if (weaponEnchantment.hasPrefix()){
                n = weaponEnchantment.getPrefix() + " " + n;
            }
            if (weaponEnchantment.hasSuffix()){
                n += " " + weaponEnchantment.getSuffix();
            }
        }
        return n;
    }

}
