package game.gameobjects.slots;

import java.util.function.Predicate;

import game.gameobjects.items.Item;

public class PredicateWrapper extends ItemSlot {

    private final ItemSlot slot;
    private Predicate<Item> predicate;

    public PredicateWrapper(ItemSlot slot, Predicate<Item> predicate) {
        this.slot = slot;
        this.predicate = predicate;
    }

    public PredicateWrapper(ItemSlot slot) {
        this(slot, null);
    }

    public boolean equals(Object obj) {
        return slot.equals(obj);
    }

    public Item getItem() {
        return slot.getItem();
    }

    public int hashCode() {
        return slot.hashCode();
    }

    public Item setItem(Item item) {
        return slot.setItem(item);
    }

    public String getName() {
        return slot.getName();
    }

    public void setName(String name) {
        slot.setName(name);
    }

    public String toString() {
        return slot.toString();
    }

    public ItemSlot setPredicate(Predicate<Item> predicate) {
        if (this.predicate != null) {
            this.predicate = this.predicate.and(predicate);
        } else {
            this.predicate = predicate;
        }
        return this;
    }

    @Override
    public boolean isValid(Item item) {
        return slot.isValid(item) && predicate.test(item);
    }

}
