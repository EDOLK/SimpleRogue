package game.gameobjects.slots;

import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;

public class WeaponSlot extends ItemSlot {

    private double chance = 1.0d;

    public WeaponSlot(Item item, String name, double chance) {
        super(item, name);
        this.chance = chance;
    }

    public WeaponSlot(String name, double chance) {
        this(null, name, chance);
    }

    public WeaponSlot(double chance) {
        this(null, "Placeholder", chance);
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        if (chance <= 1 && chance >= 0) {
            this.chance = chance;
        }
    }

    @Override
    public Weapon getItem() {
        return (Weapon)super.getItem();
    }

    @Override
    public boolean isValid(Item item) {
        return super.isValid(item) && item instanceof Weapon;
    }

}
