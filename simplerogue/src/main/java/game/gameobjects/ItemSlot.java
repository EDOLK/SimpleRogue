package game.gameobjects;

import game.gameobjects.items.Item;

public class ItemSlot {

    private Item equippedItem;
    private String itemSlotName;

    public ItemSlot() {

    }

    public ItemSlot(String itemSlotName) {
        this.itemSlotName = itemSlotName;
    }

    public ItemSlot(Item equippedItem, String itemSlotName) {
        this.equippedItem = equippedItem;
        this.itemSlotName = itemSlotName;
    }

    public Item getEquippedItem() {
        return equippedItem;
    }

    public Item setEquippedItem(Item equippedItem) {
        Item prevItem = this.equippedItem;
        this.equippedItem = equippedItem;
        return prevItem;
    }

    public String getItemSlotName() {
        return itemSlotName;
    }

    public void setItemSlotName(String itemSlotName) {
        this.itemSlotName = itemSlotName;
    }
    
}
