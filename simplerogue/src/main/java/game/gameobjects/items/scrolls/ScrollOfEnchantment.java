package game.gameobjects.items.scrolls;

import org.hexworks.zircon.api.color.TileColor;

import game.display.Display;
import game.display.ItemSelectMenu;
import game.floorgeneration.Pools;
import game.gamelogic.Flammable;
import game.gamelogic.HasInventory;
import game.gamelogic.Scrollable;
import game.gamelogic.SelfAware;
import game.gamelogic.Upgradable;
import game.gamelogic.Upgrader;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.terrains.Fire;

public class ScrollOfEnchantment extends Item implements SelfAware, Scrollable, Flammable, Upgrader {

    private Space currentSpace;

    public ScrollOfEnchantment(){
        setCharacter('\"');
        setfGColor(TileColor.create(255, 184, 133, 255));
        setbGColor(TileColor.transparent());
        setTileName("Scroll");
        setDescription("A scroll of enchantment. Very flammable.");
        setName("Scroll of enchantment");
        setWeight(1);
    }

    @Override
    public int getFuelValue() {
        return 1;
    }

    @Override
    public void onBurn(Fire fire) {
        Display.log("The " + getName() + " burns up.", currentSpace);
        getSpace().remove(this);
    }

    @Override
    public boolean read(Entity reader) {
        if (reader instanceof HasInventory hasInventory){
            Display.setMenu(ItemSelectMenu.createUpgradeMenu(this,hasInventory));
            return true;
        }
        return false;
    }

    @Override
    public boolean doUpgrade(Upgradable upgradable) {
        if (upgradable instanceof Armor armor){
            armor.setEnchantment(Pools.getRandom(Pools.ARMOR_ENCHANTMENT_LIST));
            return true;
        }
        if (upgradable instanceof Weapon weapon){
            weapon.setEnchantment(Pools.getRandom(Pools.WEAPON_ENCHANTMENT_LIST));
            return true;
        }
        return false;
    }

    @Override
    public boolean opensMenu() {
        return true;
    }

    @Override
    public Space getSpace() {
        return currentSpace;
    }

    @Override
    public void setSpace(Space space) {
        this.currentSpace = space;
    }
    
}
