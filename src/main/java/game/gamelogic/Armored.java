package game.gamelogic;

import java.util.ArrayList;
import java.util.List;

import game.gameobjects.items.armor.Armor;
import game.gameobjects.slots.ArmorSlot;

public interface Armored{
    public List<ArmorSlot> getArmorSlots();
    default List<Armor> getArmor(){
        List<Armor> armors = new ArrayList<Armor>();
        for (ArmorSlot armorSlot : getArmorSlots()) {
            if (armorSlot.getItem() != null){
                armors.add(armorSlot.getItem());
            }
        }
        return armors;
    }
    public boolean dropsEquipedArmorsOnKill();
}
