package game.floorgeneration;

import game.gameobjects.items.Item;
import game.gameobjects.items.Torch;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.armor.Chainmail;
import game.gameobjects.items.armor.Cloak;
import game.gameobjects.items.armor.IronGreaves;
import game.gameobjects.items.armor.IronHelm;
import game.gameobjects.items.armor.LeatherArmor;
import game.gameobjects.items.armor.LeatherCap;
import game.gameobjects.items.armor.LeatherGloves;
import game.gameobjects.items.armor.LeatherPants;
import game.gameobjects.items.armor.PlateArmor;
import game.gameobjects.items.armor.PlateGauntlets;
import game.gameobjects.items.potions.FirePotion;
import game.gameobjects.items.potions.HealingPotion;
import game.gameobjects.items.scrolls.ScrollOfUpgrade;
import game.gameobjects.items.weapons.BoStaff;
import game.gameobjects.items.weapons.Dagger;
import game.gameobjects.items.weapons.HandAxe;
import game.gameobjects.items.weapons.ShortSword;
import game.gameobjects.items.weapons.Weapon;

public class ItemFactory{

    protected Item item;

    public static final Item createItem(ItemIdentifier identifier){
        switch (identifier) {
            case BO_STAFF:
                return new BoStaff();
            case CHAINMAIL:
                return new Chainmail();
            case CLOAK:
                return new Cloak();
            case DAGGER:
                return new Dagger();
            case FIRE_POTION:
                return new FirePotion();
            case HANDAXE:
                return new HandAxe();
            case HEALING_POTION:
                return new HealingPotion();
            case IRON_GREAVES:
                return new IronGreaves();
            case IRON_HELM:
                return new IronHelm();
            case LEATHER_ARMOR:
                return new LeatherArmor();
            case LEATHER_CAP:
                return new LeatherCap();
            case LEATHER_GLOVES:
                return new LeatherGloves();
            case LEATHER_PANTS:
                return new LeatherPants();
            case PLATE_ARMOR:
                return new PlateArmor();
            case PLATE_GAUNTLETS:
                return new PlateGauntlets();
            case SHORTSWORD:
                return new ShortSword();
            case SCROLL_OF_UPGRADE:
                return new ScrollOfUpgrade();
            case TORCH:
                return new Torch(false);
            default:
                return null;
        }
    }
    
    public final ItemFactory CreateFactory(ItemIdentifier identifier){
        Item item = createItem(identifier);
        if (item instanceof Armor armor){
            return new ArmorFactory(armor);
        }
        if (item instanceof Weapon weapon){
            return new WeaponFactory(weapon);
        }
        this.item = item;
        return this;
    }

    public Item create(){
        return this.item;
    }

    public enum ItemIdentifier{
        SHORTSWORD,
        HANDAXE,
        DAGGER,
        BO_STAFF,
        FIRE_POTION,
        HEALING_POTION,
        CHAINMAIL,
        CLOAK,
        IRON_GREAVES,
        IRON_HELM,
        LEATHER_ARMOR,
        LEATHER_CAP,
        LEATHER_GLOVES,
        LEATHER_PANTS,
        PLATE_ARMOR,
        PLATE_GAUNTLETS,
        SCROLL_OF_UPGRADE,
        TORCH,
    }
}
