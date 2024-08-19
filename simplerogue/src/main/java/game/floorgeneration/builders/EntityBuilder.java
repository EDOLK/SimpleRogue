package game.floorgeneration.builders;

import game.gamelogic.Armed;
import game.gamelogic.Armored;
import game.gamelogic.HasInventory;
import game.gamelogic.Levelable;
import game.gameobjects.ArmorSlot;
import game.gameobjects.WeaponSlot;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Status;

public class EntityBuilder extends Builder<Entity>{
    
    public EntityBuilder(Class<? extends Entity> tClass, Object... args) {
        super(tClass, args);
    }
    
    public EntityBuilder(Entity entity){
        super(entity);
    }

    public EntityBuilder overrideArmor(Armor... armors){
        if (this.t instanceof Armored armoredEntity){
            outer:
            for (Armor armor : armors) {
                for (ArmorSlot armorSlot : armoredEntity.getArmorSlots()) {
                    try {
                        armorSlot.setEquippedArmor(armor);
                        continue outer;
                    } catch (Exception e) {

                    }
                }
            }
        }
        return this;
    }
    
    public EntityBuilder addArmor(Armor... armors){
        if (this.t instanceof Armored armoredEntity){
            outer:
            for (Armor armor : armors) {
                for (ArmorSlot armorSlot : armoredEntity.getArmorSlots()) {
                    Armor oldArmor = armorSlot.getEquippedArmor();
                    if (oldArmor == null){
                        try {
                            armorSlot.setEquippedArmor(armor);
                            continue outer;
                        } catch (Exception e) {

                        }
                    }
                }
            }
        }
        return this;
    }

    public EntityBuilder overrideWeapons(Weapon... weapons){
        if (this.t instanceof Armed armedEntity){
            outer:
            for (Weapon weapon : weapons) {
                for (WeaponSlot weaponSlot : armedEntity.getWeaponSlots()) {
                    weaponSlot.setEquippedWeapon(weapon);
                    continue outer;
                }
            }
        }
        return this;
    }

    public EntityBuilder addWeapons(Weapon... weapons){
        if (this.t instanceof Armed armedEntity){
            outer:
            for (Weapon weapon : weapons) {
                for (WeaponSlot weaponSlot : armedEntity.getWeaponSlots()) {
                    Weapon oldWeapon = weaponSlot.getEquippedWeapon();
                    if (oldWeapon == null){
                        weaponSlot.setEquippedWeapon(weapon);
                        continue outer;
                    }
                }
            }
        }
        return this;
    }
    
    public EntityBuilder withStatuses(Status... statuses){
        for (Status status : statuses) {
            t.addStatus(status);
        }
        return this;
    }
    
    public EntityBuilder withLevel(int level){
        if (this.t instanceof Levelable levelable){
            levelable.setLevel(level);
        }
        return this;
    }
    
    public EntityBuilder addToInventory(Item... items){
        if (this.t instanceof HasInventory hasInventory){
            for (Item item : items) {
                if (!hasInventory.addItemToInventory(item)){
                    break;
                }
            }
        }
        return this;
    }

}
