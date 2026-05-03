package game.display;

import java.util.function.BiFunction;
import java.util.function.Consumer;

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
import game.gameobjects.entities.Entity;
import game.gameobjects.slots.ItemSlot;

public class EquipmentMenu extends Menu{

    private Entity entity;
    private BiFunction<ComponentEvent, ItemSlot, UIEventResponse> func;

    public EquipmentMenu(Entity entity, BiFunction<ComponentEvent, ItemSlot, UIEventResponse> func){
        super();
        this.entity = entity;
        this.func = func;
    }

    public static EquipmentMenu createExamineEquipmentMenu(Entity entity){
        return createGenericEquipmentMenu(entity, (event, slot) -> {
            if (slot.getItem() != null){
                Display.setMenu(new ExamineMenu(slot.getItem()));
            }
            return UIEventResponse.processed();
        });
    }
    
    public static EquipmentMenu createEquipEquipmentMenu(Entity entity){
        return createGenericEquipmentMenu(entity, (event, slot) -> {
            Display.setMenu(ItemSelectMenu.createItemSlotMenu(slot, entity));
            return UIEventResponse.processed();
        });
    }

    public static EquipmentMenu createGenericEquipmentMenu(Entity entity, BiFunction<ComponentEvent, ItemSlot, UIEventResponse> func){
        EquipmentMenu menu = new EquipmentMenu(entity, func);
        Panel equipmentPanel = PanelBuilder.newBuilder()
            .withPosition(Position.create(menu.screen.getWidth()/2 - (menu.screen.getWidth()/3/2), menu.screen.getHeight()/2 - (menu.screen.getHeight()/3/2)))
            .withSize(menu.screen.getWidth()/3, menu.screen.getHeight()/3)
            .withDecorations(ComponentDecorations.box(BoxType.SINGLE, "Equipment"))
            .build();

        menu.screen.addComponent(equipmentPanel);

        WrappedInteger pos = new WrappedInteger(0);

        Consumer<ItemSlot> slotConsumer = (slot) -> {
            Header header = HeaderBuilder.newBuilder()
                .withText(slot.getName() + ":")
                .withPosition(0,pos.getValue())
                .build();
            pos.increment();
            equipmentPanel.addComponent(header);
            Button weaponButton = ButtonBuilder.newBuilder()
                .withText(slot.getItem() != null ? slot.getItem().getName() : "Nothing")
                .withSize(equipmentPanel.getWidth()-3, 1)
                .withDecorations()
                .withPosition(1, pos.getValue())
                .build();
            weaponButton.handleComponentEvents(ComponentEventType.ACTIVATED,(event) -> {
                return func.apply(event, slot);
            });
            equipmentPanel.addComponent(weaponButton);
            pos.increment();
        };

        if (entity instanceof Armed armed) {
            armed.getWeaponSlots().forEach(slotConsumer);
        }
        if (entity instanceof HasOffHand hoh) {
            slotConsumer.accept(hoh.getOffHandSlot());
        }
        if (entity instanceof Armored armored) {
            armored.getArmorSlots().forEach(slotConsumer);
        }

        return menu;
    }
    
    @Override
    public Menu refresh() {
        return createGenericEquipmentMenu(this.entity, this.func);
    }

}
