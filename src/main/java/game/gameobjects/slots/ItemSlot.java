package game.gameobjects.slots;

import game.gameobjects.items.Item;

public class ItemSlot {

    private Item item;
    private String name;

    public ItemSlot() {

    }

    public ItemSlot(String name) {
        this.name = name;
    }

    public ItemSlot(Item item, String name) {
        this.item = item;
        this.name = name;
    }

    public Item getItem() {
        return item;
    }

    public Item setItem(Item item) {
        Item prevItem = this.item;
        this.item = item;
        return prevItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isValid(Item item){
        return true;
    };
}
