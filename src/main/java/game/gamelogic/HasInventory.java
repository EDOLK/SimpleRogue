package game.gamelogic;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import game.gameobjects.ItemStack;
import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;

public interface HasInventory {

    public List<Item> getInventory();

    public int getHardWeightLimit();

    default Set<ItemStack> getStacks(){
        Set<ItemStack> stacks = new LinkedHashSet<>();
        outer:
        for (Item item : getInventory()) {
            for (ItemStack itemStack : stacks) {
                if(itemStack.canStack(item)){
                    itemStack.setAmount(itemStack.getAmount()+1);
                    continue outer;
                }
            }
            stacks.add(new ItemStack(item, 1));
        }
        return stacks;
    }

    default int getSoftWeightLimit(){
        return getHardWeightLimit()/2;
    };

    default boolean addItemToInventory(Item item){
        if (item != null){
            if (getInventoryWeight() + item.getWeight() <= getHardWeightLimit()){
                return getInventory().add(item);
            }
        }
        return false;
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
        if (this instanceof HasOffHand hasOffHand) {
            if (hasOffHand.getOffHandSlot().getEquippedItem() != null) {
                w += hasOffHand.getOffHandSlot().getEquippedItem().getWeight();
            }
        }
        return w;
    }
}
