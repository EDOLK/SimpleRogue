package game.gamelogic;

import java.util.List;
import java.util.Set;

import game.gameobjects.ItemStack;
import game.gameobjects.items.Item;

public interface HasInventory {

    public List<Item> getInventory();

    public int getHardWeightLimit();

    default Set<ItemStack> getStacks(){
        return ItemStack.toItemStackSet(getInventory());
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
        w += getInventory().stream().mapToInt(i -> i.getWeight()).sum();
        if (this instanceof Armored armored){
            w += armored.getArmor().stream().mapToInt(i -> i.getWeight()).sum();
        }
        if (this instanceof Armed armed){
            w += armed.getWeapons().stream().mapToInt(i -> i.getWeight()).sum();
        }
        if (this instanceof HasOffHand hasOffHand) {
            w += hasOffHand.getOffHandSlot().getEquippedItem() != null ? hasOffHand.getOffHandSlot().getEquippedItem().getWeight() : 0;
        }
        return w;
    }
}
