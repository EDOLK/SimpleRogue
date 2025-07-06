package game.gamelogic;

import java.util.ArrayList;
import java.util.List;

import game.gameobjects.ArmorSlot;
import game.gameobjects.items.armor.Armor;

public interface Armored{
    public List<ArmorSlot> getArmorSlots();
    default List<Armor> getArmor(){
        List<Armor> armors = new ArrayList<Armor>();
        for (ArmorSlot armorSlot : getArmorSlots()) {
            if (armorSlot.getEquippedArmor() != null){
                armors.add(armorSlot.getEquippedArmor());
            }
        }
        return armors;
    }
    public boolean dropsEquipedArmorsOnKill();
}
