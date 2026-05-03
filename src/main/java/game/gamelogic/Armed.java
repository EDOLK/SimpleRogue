package game.gamelogic;

import java.util.ArrayList;
import java.util.List;

import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.slots.WeaponSlot;

public interface Armed{
    public List<WeaponSlot> getWeaponSlots();
    default List<Weapon> getWeapons(){
        List<Weapon> weapons = new ArrayList<Weapon>();
        for (WeaponSlot weaponSlot : getWeaponSlots()) {
            if (weaponSlot.getItem() != null){
                weapons.add(weaponSlot.getItem());
            }
        }
        return weapons;
    };
    public boolean dropsEquippedWeaponsOnKill();
}
