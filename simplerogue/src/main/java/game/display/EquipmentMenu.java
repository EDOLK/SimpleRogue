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
import game.gameobjects.ArmorSlot;
import game.gameobjects.WeaponSlot;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.armor.Armor;

public class EquipmentMenu extends Menu{

    public EquipmentMenu(Entity entity){
        super();
    }
    
    public static EquipmentMenu createExamineEquipmentMenu(Entity entity){
        return examineMenuHelper(entity);
    }
    
    public static EquipmentMenu createEquipEquipmentMenu(Entity entity){
        return equipmentMenuHelper(entity);
    }
    
    private static EquipmentMenu examineMenuHelper(Entity entity){
        EquipmentMenu equipmentMenu = new EquipmentMenu(entity);

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
                    .withPosition(pos,0)
                    .build();
                pos++;
                equipmentPanel.addComponent(weaponHeader);
                Button weaponButton = ButtonBuilder.newBuilder()
                    .withText(weaponSlot.getEquippedWeapon() != null ? weaponSlot.getEquippedWeapon().getName() : "Nothing")
                    .withSize(equipmentPanel.getWidth()-3, 1)
                    .withDecorations()
                    .withPosition(pos, 1)
                    .build();
                // weaponButton.handleComponentEvents(ComponentEventType.ACTIVATED, weaponFunction.withButton(weaponButton).withSlot(weaponSlot).build());
                weaponButton.handleComponentEvents(ComponentEventType.ACTIVATED, (componentEvent) -> {
                    if (weaponSlot.getEquippedWeapon() != null){
                        Display.setMenu(new ExamineMenu(weaponSlot.getEquippedWeapon()));
                    }
                    return UIEventResponse.processed();
                });
                equipmentPanel.addComponent(weaponButton);
                pos ++;
            }
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
                
                // armorButton.handleComponentEvents(ComponentEventType.ACTIVATED, armorFunction.withButton(armorButton).withSlot(armorSlot).build());
                armorButton.handleComponentEvents(ComponentEventType.ACTIVATED, (componentEvent) -> {
                    if (equipedArmor != null){
                        Display.setMenu(new ExamineMenu(equipedArmor));
                    }
                    return UIEventResponse.processed();
                });
                equipmentPanel.addComponent(armorButton);

                pos++;
            }
        }
        return equipmentMenu;
    }

    private static EquipmentMenu equipmentMenuHelper(Entity entity){
        EquipmentMenu equipmentMenu = new EquipmentMenu(entity);

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
                    .withPosition(pos,0)
                    .build();
                pos++;
                equipmentPanel.addComponent(weaponHeader);
                Button weaponButton = ButtonBuilder.newBuilder()
                    .withText(weaponSlot.getEquippedWeapon() != null ? weaponSlot.getEquippedWeapon().getName() : "Nothing")
                    .withSize(equipmentPanel.getWidth()-3, 1)
                    .withDecorations()
                    .withPosition(pos, 1)
                    .build();
                // weaponButton.handleComponentEvents(ComponentEventType.ACTIVATED, weaponFunction.withButton(weaponButton).withSlot(weaponSlot).build());
                weaponButton.handleComponentEvents(ComponentEventType.ACTIVATED, (componentEvent) -> {
                    Display.setMenu(ItemSelectMenu.createWeaponSelectMenu(weaponButton, weaponSlot, entity));
                    return UIEventResponse.processed();
                });
                equipmentPanel.addComponent(weaponButton);
                pos ++;
            }
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
                
                // armorButton.handleComponentEvents(ComponentEventType.ACTIVATED, armorFunction.withButton(armorButton).withSlot(armorSlot).build());
                armorButton.handleComponentEvents(ComponentEventType.ACTIVATED, (componentEvent) -> {
                    Display.setMenu(ItemSelectMenu.createArmorSelectMenu(armorSlot, entity, armorButton));
                    return UIEventResponse.processed();
                });
                equipmentPanel.addComponent(armorButton);

                pos++;
            }
        }
        return equipmentMenu;
    }
    
}
