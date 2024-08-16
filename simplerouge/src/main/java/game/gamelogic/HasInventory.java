package game.gamelogic;

import java.util.List;

import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;

public interface HasInventory {

    public List<Item> getInventory();

    public int getMaxWeight();

    default boolean addItemToInventory(Item item){
        if (item != null){
            if (getInventoryWeight() + item.getWeight() <= getMaxWeight()){
                return getInventory().add(item);
            }
            return false;
        } else {
            return false;
        }
    }

    default boolean removeItemFromInventory(Item item){
        return getInventory().remove(item);
    }

    default int getInventoryWeight(){
        int w = 0;
        for (Item item : getInventory()) {
            w += item.getWeight();
        }
        if (this instanceof Armored armored){
            for (Armor armor : armored.getArmor()) {
                w += armor.getWeight();
            }
        }
        if (this instanceof Armed armed){
            for (Weapon weapon : armed.getWeapons()) {
                w += weapon.getWeight();
            }
        }
        return w;
    }
}