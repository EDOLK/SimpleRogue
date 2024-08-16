package game.gamelogic;

import java.util.ArrayList;
import java.util.List;

import game.gameobjects.WeaponSlot;
import game.gameobjects.items.weapons.Weapon;

public interface Armed{
    public List<WeaponSlot> getWeaponSlots();
    default List<Weapon> getWeapons(){
        List<Weapon> weapons = new ArrayList<Weapon>();
        for (WeaponSlot weaponSlot : getWeaponSlots()) {
            if (weaponSlot.getEquippedWeapon() != null){
                weapons.add(weaponSlot.getEquippedWeapon());
            }
        }
        return weapons;
    };
    public boolean dropsEquipedWeaponsOnKill();
}
