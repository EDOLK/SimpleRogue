package game.gameobjects;

import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.armor.ArmorType;

public class ArmorSlot {

    private final ArmorType type;
    private Armor equippedArmor;
    private String armorSlotName;

    public ArmorSlot(ArmorType type, String armorSlotName) {
        this.type = type;
        this.armorSlotName = armorSlotName;
    }

    public ArmorSlot(ArmorType type){
        this.type = type;
        equippedArmor = null;
    }

    public String getArmorSlotName() {
        return armorSlotName;
    }

    public void setArmorSlotName(String armorSlotName) {
        this.armorSlotName = armorSlotName;
    }

    public ArmorType getType() {
        return type;
    }

    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    public Armor setEquippedArmor(Armor armor) throws Exception{
        Armor previousArmor = getEquippedArmor();
        if (armor == null){
            equippedArmor = armor;
        } else if (armor.getArmorType() == type){
            equippedArmor = armor;
        } else {
            throw new Exception("Wrong armor type!");
        }
        return previousArmor;
    }
}
