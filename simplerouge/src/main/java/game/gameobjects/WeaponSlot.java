package game.gameobjects;

import game.gameobjects.items.weapons.Weapon;

public class WeaponSlot{

    private Weapon equippedWeapon;
    private String weaponSlotName;
    private double chance;

    public WeaponSlot(String weaponSlotName, double chance) {
        this.weaponSlotName = weaponSlotName;
        this.chance = chance;
    }

    public WeaponSlot(String weaponSlotName) {
        this.weaponSlotName = weaponSlotName;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        if (chance >= 0 && chance <= 1.0){
            this.chance = chance;
        }
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public Weapon setEquippedWeapon(Weapon equippedWeapon) {
        Weapon previousWeapon = this.equippedWeapon;
        this.equippedWeapon = equippedWeapon;
        return previousWeapon;
    }

    public String getWeaponSlotName() {
        return weaponSlotName;
    }

    public void setWeaponSlotName(String weaponSlotName) {
        this.weaponSlotName = weaponSlotName;
    }

}
