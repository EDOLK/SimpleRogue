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
import org.hexworks.zircon.api.uievent.ComponentEvent;
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
import kotlin.jvm.functions.Function1;

public class EquipmentMenu extends Menu{

    private Entity entity;
    private boolean equip;

    public EquipmentMenu(Entity entity){
        super();
    }
    
    public static EquipmentMenu createExamineEquipmentMenu(Entity entity){
        return createGenericEquipmentMenu(
            entity,
            false,
            (armorSlot) -> {
                return (event) -> {
                    if (armorSlot.getEquippedArmor() != null){
                        Display.setMenu(new ExamineMenu(armorSlot.getEquippedArmor()));
                    }
                    return UIEventResponse.processed();
                };
            },
            (weaponSlot) -> {
                return (event) -> {
                    if (weaponSlot.getEquippedWeapon() != null){
                        Display.setMenu(new ExamineMenu(weaponSlot.getEquippedWeapon()));
                    }
                    return UIEventResponse.processed();
                };
            },
            (itemSlot) -> {
                return (event) -> {
                    if (itemSlot.getEquippedItem() != null){
                        Display.setMenu(new ExamineMenu(itemSlot.getEquippedItem()));
                    }
                    return UIEventResponse.processed();
                };
            }
        );
    }
    
    public static EquipmentMenu createEquipEquipmentMenu(Entity entity){
        return createGenericEquipmentMenu(
            entity,
            true,
            (armorSlot) -> {
                return (event) -> {
                    Display.setMenu(ItemSelectMenu.createArmorSelectMenu(armorSlot, entity));
                    return UIEventResponse.processed();
                };
            },
            (weaponSLot) -> {
                return (event) -> {
                    Display.setMenu(ItemSelectMenu.createWeaponSelectMenu(weaponSLot, entity));
                    return UIEventResponse.processed();
                };
            },
            (itemSlot) -> {
                return (event) -> {
                    Display.setMenu(ItemSelectMenu.createItemSelectMenu(itemSlot, entity));
                    return UIEventResponse.processed();
                };
            }
        );
    }

    public static EquipmentMenu createGenericEquipmentMenu(Entity entity, boolean equip, ComponentEventFuncWithExtra<ArmorSlot> armorFunc, ComponentEventFuncWithExtra<WeaponSlot> weaponFunc, ComponentEventFuncWithExtra<ItemSlot> itemFunc){
        EquipmentMenu equipmentMenu = new EquipmentMenu(entity);
        equipmentMenu.entity = entity;
        equipmentMenu.equip = equip;
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
                weaponButton.handleComponentEvents(ComponentEventType.ACTIVATED,weaponFunc.generateFunction(weaponSlot));
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
            itemButton.handleComponentEvents(ComponentEventType.ACTIVATED,itemFunc.generateFunction(itemSlot));
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
                
                armorButton.handleComponentEvents(ComponentEventType.ACTIVATED, armorFunc.generateFunction(armorSlot));
                
                equipmentPanel.addComponent(armorButton);

                pos++;
            }
        }
        return equipmentMenu;
    }

    @Override
    public Menu refresh() {
        if (this.equip) {
            return createEquipEquipmentMenu(this.entity);
        } else {
            return createExamineEquipmentMenu(this.entity);
        }
    }

    @FunctionalInterface
    public static interface ComponentEventFuncWithExtra <T>{

        public Function1<? super ComponentEvent, ? extends UIEventResponse> get(T t);

        default Function1<? super ComponentEvent, ? extends UIEventResponse> generateFunction(T t){
            return (event) -> {
                return get(t).invoke(event);
            };
        };
        
    }
    
}
