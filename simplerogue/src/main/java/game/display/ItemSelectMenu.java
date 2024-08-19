package game.display;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.hexworks.zircon.api.ComponentDecorations;
import org.hexworks.zircon.api.builder.component.VBoxBuilder;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.Container;
import org.hexworks.zircon.api.component.VBox;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.uievent.KeyCode;
import org.hexworks.zircon.api.uievent.KeyboardEvent;
import org.hexworks.zircon.api.uievent.UIEventPhase;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.display.FloorMenu.State;
import game.gamelogic.Aimable;
import game.gamelogic.Consumable;
import game.gamelogic.Examinable;
import game.gamelogic.HasInventory;
import game.gamelogic.Upgradable;
import game.gamelogic.Upgrader;
import game.gameobjects.ArmorSlot;
import game.gameobjects.Space;
import game.gameobjects.WeaponSlot;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;

public class ItemSelectMenu extends Menu{

    private VBox createVbox(){
        return VBoxBuilder.newBuilder()
            .withPosition(Position.create(screen.getWidth()/2 - (screen.getWidth()/3/2), screen.getHeight()/2 - (screen.getHeight()/3/2)))
            .withSize(screen.getWidth()/3, screen.getHeight()/3)
            .withDecorations(ComponentDecorations.box(BoxType.SINGLE, "Select"))
            .build();
    }
    
    public ItemSelectMenu(Boolean withVbox){
        super();
        if (withVbox) {
            this.screen.addComponent(createVbox());
        }
    }
    
    public ItemSelectMenu(){
        this(false);
    }

    public static ItemSelectMenu createDropMenu(Space space, HasInventory dropper){
        ItemSelectMenu itemSelectMenu = new ItemSelectMenu(false);
        Container container = Display.createFittedContainer(itemSelectMenu.screen, "Dropping", dropper.getInventory());
        Function<Item, UIEventResponse> function = (item) ->{
            dropper.removeItemFromInventory(item);
            space.addItem(item);
            Display.update();
            Display.revertMenu();
            Display.setMenu(ItemSelectMenu.createDropMenu(space, dropper));
            return UIEventResponse.processed();
        };
        Display.populateContainer(container, function, dropper.getInventory());
        itemSelectMenu.screen.addComponent(container);
        return itemSelectMenu;
    }
    
    public static ItemSelectMenu createPickupMenu(Space space, HasInventory pickUpper){
        ItemSelectMenu itemSelectMenu = new ItemSelectMenu(false);

        Function<Item, UIEventResponse> function = (item) ->{
            if (pickUpper.addItemToInventory(item)){
                space.getItems().remove(item);
                Display.log("Picked up the " + item.getName() + ".");
            } else {
                Display.log("The " + item.getName() + " is too heavy.");
            }

            Display.revertMenu();
            Display.setMenu(createPickupMenu(space, pickUpper));
            Display.update();

            return UIEventResponse.processed();
        };
        Display.populateMenu(itemSelectMenu, function, "Pickup", space.getItems());
        return itemSelectMenu;
    }
    
    public static ItemSelectMenu createInventoryTransferMenu(HasInventory fromInventory, HasInventory toInventory){
        ItemSelectMenu menu = new ItemSelectMenu();
        Function<Item, UIEventResponse> function = (item) ->{
            if (toInventory.addItemToInventory(item)){
                fromInventory.removeItemFromInventory(item);
                Display.log("Took the " + item.getName() + ".");
            } else {
                Display.log("The " + item.getName() + " is too heavy.");
            }
            Display.revertMenu();
            Display.setMenu(createInventoryTransferMenu(fromInventory, toInventory));
            Display.update();
            return UIEventResponse.processed();
        };
        String name = "Pickup";
        if (fromInventory instanceof Examinable examinable){
            name = examinable.getName();
        }
        Display.populateMenu(menu, function, name, fromInventory.getInventory());
        return menu;
    }
    
    public static ItemSelectMenu createConsumableSelectMenu(Entity consumer){
        ItemSelectMenu itemSelectMenu = new ItemSelectMenu(false);

        ArrayList<Item> items = new ArrayList<Item>();
        if (consumer instanceof HasInventory hasInventory){
            for (Item item : hasInventory.getInventory()) {
                if (item instanceof Consumable){
                    items.add(item);
                }
            }
        }
        
        Function<Item,UIEventResponse> function = (i) -> {
            if (i instanceof Consumable c){
                if (c.consume(consumer) && consumer instanceof HasInventory hasInventory){
                    hasInventory.removeItemFromInventory(i);
                }
            }
            Display.revertMenu();
            return UIEventResponse.processed();
        };
        Display.populateMenu(itemSelectMenu, function, "Consume", items);

        return itemSelectMenu;
    }
    
    public static ItemSelectMenu createWeaponSelectMenu(Button button, WeaponSlot weaponSlot, Entity entity){
        ItemSelectMenu itemSelectMenu = new ItemSelectMenu(false);
        ArrayList<Weapon> weapons = new ArrayList<Weapon>();
        if (entity instanceof HasInventory hasInventory){
            for (Item item : hasInventory.getInventory()) {
                if (item instanceof Weapon weapon1){
                    weapons.add(weapon1);
                }
            }
        }

        Container container = Display.createFittedContainer(itemSelectMenu.screen, "Select", weapons);
        
        Item nothing = new Item();
        nothing.setName("Nothing");
        
        Function<Item, UIEventResponse> nothingFunction = nonItem ->{
            button.setText("Nothing");
            if (entity instanceof HasInventory hasInventory){
                hasInventory.addItemToInventory(weaponSlot.setEquippedWeapon(null));
            } else {
                weaponSlot.setEquippedWeapon(null);
            }
            Display.revertMenu();
            return UIEventResponse.processed();
        };

        Display.populateContainer(container, nothingFunction, nothing);
        
        Function<Weapon, UIEventResponse> function = newWeapon -> {
            button.setText(newWeapon.getName());
            if (entity instanceof HasInventory hasInventory){
                hasInventory.removeItemFromInventory(newWeapon);
                hasInventory.addItemToInventory(weaponSlot.setEquippedWeapon(newWeapon));
            } else {
                weaponSlot.setEquippedWeapon(newWeapon);
            }
            Display.revertMenu();
            return UIEventResponse.processed();
        };
        
        Display.populateContainer(container, function, weapons);
        itemSelectMenu.screen.addComponent(container);

        return itemSelectMenu;
    }
    
    public static ItemSelectMenu createArmorSelectMenu(ArmorSlot armorSlot, Entity entity, Button button){
        ItemSelectMenu itemSelectMenu = new ItemSelectMenu(false);
        
        ArrayList<Armor> armors = new ArrayList<Armor>();
        if (entity instanceof HasInventory hasInventory){
            for (Item item : hasInventory.getInventory()) {
                if (item instanceof Armor armor){
                    if (armor.getArmorType() == armorSlot.getType()){
                        armors.add(armor);
                    }
                }
            }
        }

        Container container = Display.createFittedContainer(itemSelectMenu.screen, "Select", armors);
        
        Item nothing = new Item();
        nothing.setName("Nothing");
        
        Function<Item, UIEventResponse> nothingFunction = nonItem ->{
            button.setText("Nothing");
            try {
                if (entity instanceof HasInventory hasInventory){
                    hasInventory.addItemToInventory(armorSlot.setEquippedArmor(null));
                } else {
                    armorSlot.setEquippedArmor(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Display.revertMenu();
            return UIEventResponse.processed();
        };

        Display.populateContainer(container, nothingFunction, nothing);

        Function<Armor, UIEventResponse> armorFunction = new Function<Armor,UIEventResponse>() {
            @Override
            public UIEventResponse apply(Armor armor){
                button.setText(armor.getName());
                try {
                    if (entity instanceof HasInventory hasInventory){
                        hasInventory.removeItemFromInventory(armor);
                        hasInventory.addItemToInventory(armorSlot.setEquippedArmor(armor));
                    } else {
                        armorSlot.setEquippedArmor(armor);
                    }
                } catch (Exception e) {
                    Display.log(e.getMessage());
                }
                Display.revertMenu();
                return UIEventResponse.processed();
            }
        };

        Display.populateContainer(container, armorFunction, armors);

        itemSelectMenu.screen.addComponent(container);

        return itemSelectMenu;
    }

    public static ItemSelectMenu createThrowMenu(HasInventory hasInventory){
        ItemSelectMenu itemSelectMenu = new ItemSelectMenu();
        List<Item> aimables = new ArrayList<Item>();
        for (Item item : hasInventory.getInventory()) {
            if (item instanceof Aimable){
                aimables.add(item);
            }
        }

        Container container = Display.createFittedContainer(itemSelectMenu.screen, "Throw", aimables);

        Function<Item, UIEventResponse> function = (item) ->{
            FloorMenu floorMenu = new FloorMenu();
            Display.setMenu(floorMenu);
            floorMenu.setCurrentState(State.AIMING);
            floorMenu.setThowingItem((Aimable)item);
            floorMenu.toggleExamination();
            floorMenu.update();
            return UIEventResponse.processed();
        };
        
        Display.populateContainer(container, function, aimables);

        itemSelectMenu.screen.addComponent(container);

        return itemSelectMenu;
    }
    
    public static ItemSelectMenu createInventoryMenu(PlayerEntity playerEntity){
        ItemSelectMenu itemSelectMenu = new ItemSelectMenu();
        List<Item> list = playerEntity.getInventory();
        Container container = Display.createFittedContainer(itemSelectMenu.screen, "Inventory", list);
        Function <Item, UIEventResponse> function = (item) ->{
            Display.setMenu(new ItemContextMenu(item, playerEntity));
            return UIEventResponse.processed();
        };
        Display.populateContainer(container, function, list);
        itemSelectMenu.screen.addComponent(container);
        return itemSelectMenu;
    }
    
    public static ItemSelectMenu createUpgradeMenu(Upgrader upgrader, HasInventory hasInventory){
        ItemSelectMenu itemSelectMenu = new ItemSelectMenu();
        List<Item> list = new ArrayList<Item>();
        for (Item item : hasInventory.getInventory()) {
            if (item instanceof Upgradable upgradable && upgradable.canUpgrade(upgrader)){
                list.add(item);
            }
        }
        Container container = Display.createFittedContainer(itemSelectMenu.screen, "Upgrade", list);
        Function <Item, UIEventResponse> function = (item) ->{
            if (item instanceof Upgradable upgradable){
                if (upgradable.upgrade(upgrader)){
                    hasInventory.removeItemFromInventory((Item)upgrader);
                }
            }
            Display.setAndForgetMenus(Display.getFloorMenu());
            return UIEventResponse.processed();
        };
        Display.populateContainer(container, function, list);
        itemSelectMenu.screen.addComponent(container);
        return itemSelectMenu;
    }

    @Override
    public UIEventResponse handleKeyboardEvent(KeyboardEvent event, UIEventPhase phase) {
        if (event.getCode() == KeyCode.ESCAPE){
            Display.revertMenu();
            return UIEventResponse.processed();
        }
        return UIEventResponse.pass();
    }
}
