package game.gameobjects.items.armor;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.HasDodge;
import game.gamelogic.HasResistances;
import game.gamelogic.Levelable;
import game.gamelogic.Upgradable;
import game.gamelogic.Upgrader;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.enchantments.ArmorEnchantment;
import game.gameobjects.items.Item;
import game.gameobjects.items.scrolls.ScrollOfEnchantment;
import game.gameobjects.items.scrolls.ScrollOfUpgrade;

public class Armor extends Item implements HasResistances, HasDodge, Levelable, Upgradable{

    private ArmorType armorType;
    private List<Resistance> resistances = new ArrayList<Resistance>();
    private int dodge = 0;
    private int level = 1;
    private ArmorEnchantment enchantment;

    public ArmorEnchantment getEnchantment() {
        return enchantment;
    }

    public void setEnchantment(ArmorEnchantment enchantment) {
        this.enchantment = enchantment;
    }

    public Armor(TileColor bGColor, TileColor fGColor, char character) {
        super(bGColor, fGColor, character);
    }

    @Override
    public String getName() {
        String s = super.getName();
        if (enchantment != null){
            s = enchantment.hasPrefix() ? enchantment.getPrefix() + " " + s : s;
            s = enchantment.hasSuffix() ? s + " " + enchantment.getSuffix() : s;
        }
        return s;
    }

    public Armor(){
        super();
    }

    public ArmorType getArmorType() {
        return armorType;
    }

    public void setArmorType(ArmorType armorType) {
        this.armorType = armorType;
    }

    @Override
    public List<Resistance> getResistances() {
        return resistances;
    }

    @Override
    public int getDodge() {
        return dodge;
    }

    public void setDodge(int dodge) {
        this.dodge = dodge;
    }

    @Override
    public boolean upgrade(Upgrader upgrader) {
        return upgrader.doUpgrade(this);
    }

    @Override
    public int getLevel() {
        return level;
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
    public boolean canStack(Item otherItem) {
        if (otherItem instanceof Armor armor) {
            return this.level == armor.level && this.enchantment == armor.enchantment;
        }
        return super.canStack(otherItem);
    }

}
