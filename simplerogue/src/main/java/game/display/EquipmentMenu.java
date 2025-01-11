package game.display;

import org.hexworks.zircon.api.ComponentDecorations;
import org.hexworks.zircon.api.builder.component.ButtonBuilder;
import org.hexworks.zircon.api.builder.component.HeaderBuilder;
import org.hexworks.zircon.api.builder.component.PanelBuilder;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.Header;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.uievent.ComponentEventType;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.gamelogic.Armed;
import game.gamelogic.Armored;
import game.gamelogic.HasOffHand;
import game.gameobjects.ArmorSlot;
import game.gameobjects.ItemSlot;
import game.gameobjects.WeaponSlot;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.armor.Armor;

public class EquipmentMenu extends Menu{

    private Entity entity;
    private boolean equip;

    public EquipmentMenu(Entity entity){
        super();
    }
    
    public static EquipmentMenu createExamineEquipmentMenu(Entity entity){
        return equipmentExamineMenuHelper(entity);
    }
    
    public static EquipmentMenu createEquipEquipmentMenu(Entity entity){
        return equipmentEquipMenuHelper(entity);
    }
    
    private static EquipmentMenu equipmentExamineMenuHelper(Entity entity){
        EquipmentMenu equipmentMenu = new EquipmentMenu(entity);
        equipmentMenu.entity = entity;
        equipmentMenu.equip = false;
        Panel equipmentPanel = PanelBuilder.newBuilder()
            .withPosition(Position.create(equipmentMenu.screen.getWidth()/2 - (equipmentMenu.screen.getWidth()/3/2), equipmentMenu.screen.getHeight()/2 - (equipmentMenu.screen.getHeight()/3/2)))
            .withSize(equipmentMenu.screen.getWidth()/3, equipmentMenu.screen.getHeight()/3)
            .withDecorations(ComponentDecorations.box(BoxType.SINGLE, "Equipment"))
            .build();

        equipmentMenu.screen.addComponent(equipmentPanel);

        int pos = 0;

        if (entity instanceof Armed armed){
            for (WeaponSlot weaponSlot : armed.getWeaponSlots()) {
                Header weaponHeader = HeaderBuilder.newBuilder()
                    .withText(weaponSlot.getWeaponSlotName() + ":")
                    .withPosition(0,pos)
                    .build();
                pos++;
                equipmentPanel.addComponent(weaponHeader);
                Button weaponButton = ButtonBuilder.newBuilder()
                    .withText(weaponSlot.getEquippedWeapon() != null ? weaponSlot.getEquippedWeapon().getName() : "Nothing")
                    .withSize(equipmentPanel.getWidth()-3, 1)
                    .withDecorations()
                    .withPosition(1, pos)
                    .build();
                weaponButton.handleComponentEvents(ComponentEventType.ACTIVATED, event -> {
                    if (weaponSlot.getEquippedWeapon() != null){
                        Display.setMenu(new ExamineMenu(weaponSlot.getEquippedWeapon()));
                    }
                    return UIEventResponse.processed();
                });
                equipmentPanel.addComponent(weaponButton);
                pos ++;
            }
        }

        if (entity instanceof HasOffHand hasOffHand){

            ItemSlot itemSlot = hasOffHand.getOffHandSlot();

            Header itemHeader = HeaderBuilder.newBuilder()
                .withText(itemSlot.getItemSlotName() + ":")
                .withPosition(0,pos)
                .build();
            pos++;
            equipmentPanel.addComponent(itemHeader);
            Button itemButton = ButtonBuilder.newBuilder()
                .withText(itemSlot.getEquippedItem() != null ? itemSlot.getEquippedItem().getName() : "Nothing")
                .withSize(equipmentPanel.getWidth()-3, 1)
                .withDecorations()
                .withPosition(1, pos)
                .build();
            itemButton.handleComponentEvents(ComponentEventType.ACTIVATED, event -> {
                if (itemSlot.getEquippedItem() != null){
                    Display.setMenu(new ExamineMenu(itemSlot.getEquippedItem()));
                }
                return UIEventResponse.processed();
            });
            equipmentPanel.addComponent(itemButton);
            pos ++;
        }
        
        if (entity instanceof Armored armored){
            for (int i = 0; i < armored.getArmorSlots().size(); i ++) {

                ArmorSlot armorSlot = armored.getArmorSlots().get(i);

                Header armorSlotHeader = HeaderBuilder.newBuilder()
                    .withText(armorSlot.getType().toString() + ":")
                    .withPosition(0, pos)
                    .build();
                equipmentPanel.addComponent(armorSlotHeader);

                Armor equipedArmor = armorSlot.getEquippedArmor();
                
                pos++;

                Button armorButton = ButtonBuilder.newBuilder()
                    .withText(equipedArmor != null ? equipedArmor.getName() : "Nothing")
                    .withSize(equipmentPanel.getWidth()-3, 1)
                    .withDecorations()
                    .withPosition(1, pos)
                    .build();
                
                armorButton.handleComponentEvents(ComponentEventType.ACTIVATED, event -> {
                    if (armorSlot.getEquippedArmor() != null){
                        Display.setMenu(new ExamineMenu(armorSlot.getEquippedArmor()));
                    }
                    return UIEventResponse.processed();
                });
                
                equipmentPanel.addComponent(armorButton);

                pos++;
            }
        }
        return equipmentMenu;
    }
    private static EquipmentMenu equipmentEquipMenuHelper(Entity entity){
        EquipmentMenu equipmentMenu = new EquipmentMenu(entity);
        equipmentMenu.entity = entity;
        equipmentMenu.equip = true;

        Panel equipmentPanel = PanelBuilder.newBuilder()
            .withPosition(Position.create(equipmentMenu.screen.getWidth()/2 - (equipmentMenu.screen.getWidth()/3/2), equipmentMenu.screen.getHeight()/2 - (equipmentMenu.screen.getHeight()/3/2)))
            .withSize(equipmentMenu.screen.getWidth()/3, equipmentMenu.screen.getHeight()/3)
            .withDecorations(ComponentDecorations.box(BoxType.SINGLE, "Equipment"))
            .build();

        equipmentMenu.screen.addComponent(equipmentPanel);

        int pos = 0;

        if (entity instanceof Armed armed){
            for (WeaponSlot weaponSlot : armed.getWeaponSlots()) {
                Header weaponHeader = HeaderBuilder.newBuilder()
                    .withText(weaponSlot.getWeaponSlotName() + ":")
                    .withPosition(0,pos)
                    .build();
                equipmentPanel.addComponent(weaponHeader);
                pos++;
                Button weaponButton = ButtonBuilder.newBuilder()
                    .withText(weaponSlot.getEquippedWeapon() != null ? weaponSlot.getEquippedWeapon().getName() : "Nothing")
                    .withSize(equipmentPanel.getWidth()-3, 1)
                    .withDecorations()
                    .withPosition(1, pos)
                    .build();
                weaponButton.handleComponentEvents(ComponentEventType.ACTIVATED, event -> {
                    Display.setMenu(ItemSelectMenu.createWeaponSelectMenu(weaponSlot, entity));
                    return UIEventResponse.processed();
                });
                equipmentPanel.addComponent(weaponButton);
                pos++;
            }
        }
        
        if (entity instanceof HasOffHand hasOffHand){

            ItemSlot itemSlot = hasOffHand.getOffHandSlot();
            Header itemHeader = HeaderBuilder.newBuilder()
                .withText(itemSlot.getItemSlotName() + ":")
                .withPosition(0,pos)
                .build();
            equipmentPanel.addComponent(itemHeader);
            pos++;

            Button itemButton = ButtonBuilder.newBuilder()
                .withText(itemSlot.getEquippedItem() != null ? itemSlot.getEquippedItem().getName() : "Nothing")
                .withSize(equipmentPanel.getWidth()-3, 1)
                .withDecorations()
                .withPosition(1,pos)
                .build();
            itemButton.handleComponentEvents(ComponentEventType.ACTIVATED, event -> {
                Display.setMenu(ItemSelectMenu.createItemSelectMenu(itemSlot, entity));
                return UIEventResponse.processed();
            });
            equipmentPanel.addComponent(itemButton);
            pos++;
        }

        if (entity instanceof Armored armored){
            for (int i = 0; i < armored.getArmorSlots().size(); i ++) {

                ArmorSlot armorSlot = armored.getArmorSlots().get(i);
                Header armorSlotHeader = HeaderBuilder.newBuilder()
                    .withText(armorSlot.getType().toString() + ":")
                    .withPosition(0, pos)
                    .build();
                equipmentPanel.addComponent(armorSlotHeader);
                pos++;

                Armor equipedArmor = armorSlot.getEquippedArmor();
                Button armorButton = ButtonBuilder.newBuilder()
                    .withText(equipedArmor != null ? equipedArmor.getName() : "Nothing")
                    .withSize(equipmentPanel.getWidth()-3, 1)
                    .withDecorations()
                    .withPosition(1, pos)
                    .build();
                armorButton.handleComponentEvents(ComponentEventType.ACTIVATED, event -> {
                    Display.setMenu(ItemSelectMenu.createArmorSelectMenu(armorSlot,entity));
                    return UIEventResponse.processed();
                });
                equipmentPanel.addComponent(armorButton);
                pos++;
            }
        }
        return equipmentMenu;
    }

    @Override
    public Menu refresh() {
        if (equip) {
            return equipmentEquipMenuHelper(this.entity);
        } else {
            return equipmentExamineMenuHelper(this.entity);
        }
    }
    
}
