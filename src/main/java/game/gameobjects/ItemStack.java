package game.gameobjects;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import game.gamelogic.HasName;
import game.gameobjects.items.Item;

public class ItemStack implements HasName {

    private int amount;

    private Item item;

    public ItemStack(Item item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public String getName() {
        return item.getName() + (amount > 1 ? " x" + amount : "");
    }

    public boolean canStack(Item otherItem){
        return this.item.getClass() == otherItem.getClass() && this.item.canStack(otherItem);
    }

    public static Set<ItemStack> toItemStackSet(Collection<Item> items){
        Set<ItemStack> stacks = new LinkedHashSet<>();
        outer:
        for (Item item : items) {
            for (ItemStack itemStack : stacks) {
                if(itemStack.canStack(item)){
                    itemStack.setAmount(itemStack.getAmount()+1);
                    itemStack.setItem(item);
                    continue outer;
                }
            }
            stacks.add(new ItemStack(item, 1));
        }
        return stacks;
    }

}
