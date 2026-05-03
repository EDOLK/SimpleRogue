package game.gameobjects.slots;

import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.armor.ArmorType;

public class ArmorSlot extends ItemSlot {
    private final ArmorType type;

    public ArmorType getType() {
        return type;
    }

    public ArmorSlot(Item item, String name, ArmorType type) {
        super(item, name);
        this.type = type;
    }

    public ArmorSlot(String name, ArmorType type) {
        this(null, name, type);
    }

    public ArmorSlot(ArmorType type) {
        this(type.toString(), type);
    }

    @Override
    public boolean isValid(Item item) {
        return super.isValid(item) && item instanceof Armor armor && armor.getArmorType() == type;
    }

    @Override
    public Armor getItem() {
        return (Armor)super.getItem();
    }
    
}
