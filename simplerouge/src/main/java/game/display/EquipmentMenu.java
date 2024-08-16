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
import org.hexworks.zircon.api.uievent.KeyCode;
import org.hexworks.zircon.api.uievent.KeyboardEvent;
import org.hexworks.zircon.api.uievent.UIEventPhase;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.gamelogic.Armed;
import game.gamelogic.Armored;
import game.gameobjects.ArmorSlot;
import game.gameobjects.WeaponSlot;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.armor.Armor;
import kotlin.jvm.functions.Function1;

public class EquipmentMenu extends Menu{

    public EquipmentMenu(Entity entity){
        super();
    }
    
    public static EquipmentMenu createExamineEquipmentMenu(Entity entity){
        return equipmentMenuHelper(entity, new WeaponSlotExamineFunction(entity), new ArmorSlotExamineFunction(entity));
    }
    
    public static EquipmentMenu createEquipEquipmentMenu(Entity entity){
        return equipmentMenuHelper(entity, new WeaponSlotButtonFunction(entity), new ArmorSlotButtonFunction(entity));
    }
    
    private static EquipmentMenu equipmentMenuHelper(Entity entity, WeaponSlotButtonFunction weaponFunction, ArmorSlotButtonFunction armorFunction){
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
                weaponButton.handleComponentEvents(ComponentEventType.ACTIVATED, weaponFunction.withButton(weaponButton).withSlot(weaponSlot).build());
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
                
                armorButton.handleComponentEvents(ComponentEventType.ACTIVATED, armorFunction.withButton(armorButton).withSlot(armorSlot).build());
                
                equipmentPanel.addComponent(armorButton);

                pos++;
            }
        }
        return equipmentMenu;
    }
    
    @Override
    public UIEventResponse handleKeyboardEvent(KeyboardEvent event, UIEventPhase phase) {
        if (event.getCode() == KeyCode.ESCAPE){
            Display.revertMenu();
            return UIEventResponse.processed();
        }
        return UIEventResponse.pass();
    }
    
    /**
     * ArmorSlotButtonFunction 
     */
    public static class ArmorSlotButtonFunction{
        
        protected Button armorButton;
        protected ArmorSlot armorSlot;
        protected Entity entity;

        public ArmorSlotButtonFunction(Entity entity){
            this.entity = entity;
        }
        
        public ArmorSlotButtonFunction withButton(Button button){
            this.armorButton = button;
            return this;
        }

        public ArmorSlotButtonFunction withSlot(ArmorSlot slot){
            this.armorSlot = slot;
            return this;
        }
        
        public Function1<ComponentEvent, UIEventResponse> build(){
            final ArmorSlot currentSlot = armorSlot;
            final Entity currentEntity = entity;
            final Button currentButton = armorButton;
            return new Function1<ComponentEvent,UIEventResponse>() {

                @Override
                public UIEventResponse invoke(ComponentEvent arg0) {
                    Display.setMenu(ItemSelectMenu.createArmorSelectMenu(currentSlot, currentEntity, currentButton));
                    return UIEventResponse.processed();
                }
                
            };
        }
    
    }
    /**
     * ArmorSlotExamineFunction
     */
    public static class ArmorSlotExamineFunction extends ArmorSlotButtonFunction {

        public ArmorSlotExamineFunction(Entity entity) {
            super(entity);
        }
        

        @Override
        public Function1<ComponentEvent, UIEventResponse> build() {
            final ArmorSlot currentSlot = armorSlot;
            return new Function1<ComponentEvent,UIEventResponse>() {
                @Override
                public UIEventResponse invoke(ComponentEvent arg0) {
                    if (currentSlot.getEquippedArmor() != null){
                        Display.setMenu(new ExamineMenu(currentSlot.getEquippedArmor()));
                    }
                    return UIEventResponse.processed();
                }
            };
        }
    }
    
    /**
     * WeaponSlotButtonFunction
     */
    public static class WeaponSlotButtonFunction {
    
        protected Button weaponButton;
        protected WeaponSlot weaponSlot;
        protected Entity entity;

        public WeaponSlotButtonFunction(Entity entity) {
            this.entity = entity;
        }
        
        public WeaponSlotButtonFunction withButton(Button button){
            this.weaponButton = button;
            return this;
        }

        public WeaponSlotButtonFunction withSlot(WeaponSlot slot){
            this.weaponSlot = slot;
            return this;
        }

        public Function1<ComponentEvent, UIEventResponse> build(){
            final WeaponSlot currentSlot = weaponSlot;
            final Entity currentEntity = entity;
            final Button currentButton = weaponButton;
            return new Function1<ComponentEvent,UIEventResponse>() {

                @Override
                public UIEventResponse invoke(ComponentEvent arg0) {
                    Display.setMenu(ItemSelectMenu.createWeaponSelectMenu(currentButton, currentSlot, currentEntity));
                    return UIEventResponse.processed();
                }
                
            };
        }
        
    }
    
    /**
     * WeaponSlotExamineFunction
     */
    public static class WeaponSlotExamineFunction extends WeaponSlotButtonFunction {

        public WeaponSlotExamineFunction(Entity entity) {
            super(entity);
        }

        @Override
        public Function1<ComponentEvent, UIEventResponse> build() {
            
            return new Function1<ComponentEvent,UIEventResponse>() {
                final WeaponSlot currentSlot = weaponSlot;
                @Override
                public UIEventResponse invoke(ComponentEvent arg0) {
                    if (currentSlot.getEquippedWeapon() != null){
                        Display.setMenu(new ExamineMenu(currentSlot.getEquippedWeapon()));
                    }
                    return UIEventResponse.processed();
                }

            };

        }
        
        
    }
    
}
