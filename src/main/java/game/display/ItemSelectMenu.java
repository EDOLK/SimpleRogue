package game.display;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.hexworks.zircon.api.ComponentDecorations;
import org.hexworks.zircon.api.builder.component.VBoxBuilder;
import org.hexworks.zircon.api.component.Container;
import org.hexworks.zircon.api.component.VBox;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.gamelogic.Aimable;
import game.gamelogic.Consumable;
import game.gamelogic.Examinable;
import game.gamelogic.HasInventory;
import game.gamelogic.Upgradable;
import game.gamelogic.Upgrader;
import game.gameobjects.ArmorSlot;
import game.gameobjects.ItemSlot;
import game.gameobjects.ItemStack;
import game.gameobjects.Space;
import game.gameobjects.WeaponSlot;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;

public class ItemSelectMenu extends Menu{

    private Type type;
    private HasInventory[] inventories;
    private Entity entity;
    private Space space;
    private ArmorSlot armorSlot;
    private WeaponSlot weaponSlot;
    private ItemSlot itemSlot;
    private Upgrader upgrader;


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
        itemSelectMenu.type = Type.DROP;
        itemSelectMenu.space = space;
        itemSelectMenu.inventories = new HasInventory[]{dropper};
        Container container = Display.createFittedContainer(itemSelectMenu.screen, "Dropping", dropper.getStacks());
        Function<ItemStack, UIEventResponse> function = (stack) ->{
            Item item = stack.getItem();
            dropper.removeItemFromInventory(item);
            space.addItem(item);
            Display.replaceMenu(itemSelectMenu.refresh());
            return UIEventResponse.processed();
        };
        Display.populateContainer(container, function, dropper.getStacks());
        itemSelectMenu.screen.addComponent(container);
        return itemSelectMenu;
    }
    
    public static ItemSelectMenu createPickupMenu(Space space, HasInventory pickUpper){
        ItemSelectMenu itemSelectMenu = new ItemSelectMenu(false);
        itemSelectMenu.type = Type.PICKUP;
        itemSelectMenu.space = space;
        itemSelectMenu.inventories = new HasInventory[]{pickUpper};

        Function<Item, UIEventResponse> function = (item) ->{
            if (pickUpper.addItemToInventory(item)){
                space.remove(item);
                Display.log("Picked up the " + item.getName() + ".");
            } else {
                Display.log("The " + item.getName() + " is too heavy.");
            }

            Display.replaceMenu(itemSelectMenu.refresh());

            return UIEventResponse.processed();
        };
        Display.populateMenu(itemSelectMenu, function, "Pickup", space.getItems());
        return itemSelectMenu;
    }
    
    public static ItemSelectMenu createInventoryTransferMenu(HasInventory fromInventory, HasInventory toInventory){
        ItemSelectMenu menu = new ItemSelectMenu();
        menu.type = Type.INVENTORY_TRANSFER;
        menu.inventories = new HasInventory[]{fromInventory,toInventory};
        Function<Item, UIEventResponse> function = (item) ->{
            if (toInventory.addItemToInventory(item)){
                fromInventory.removeItemFromInventory(item);
                Display.log("Took the " + item.getName() + ".");
            } else {
                Display.log("The " + item.getName() + " is too heavy.");
            }
            Display.replaceMenu(menu.refresh());
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
        itemSelectMenu.type = Type.CONSUMABLE;
        itemSelectMenu.entity = consumer;

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
    
    public static ItemSelectMenu createWeaponSelectMenu(WeaponSlot weaponSlot, Entity entity){
        ItemSelectMenu itemSelectMenu = new ItemSelectMenu(false);
        itemSelectMenu.weaponSlot = weaponSlot;
        itemSelectMenu.entity = entity;
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
    
    public static ItemSelectMenu createArmorSelectMenu(ArmorSlot armorSlot, Entity entity){
        ItemSelectMenu itemSelectMenu = new ItemSelectMenu(false);
        itemSelectMenu.armorSlot = armorSlot;
        itemSelectMenu.entity = entity;
        
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
        itemSelectMenu.type = Type.THROW;
        itemSelectMenu.inventories = new HasInventory[]{hasInventory};
        List<Item> aimables = new ArrayList<Item>();
        for (Item item : hasInventory.getInventory()) {
            if (item instanceof Aimable){
                aimables.add(item);
            }
        }

        Container container = Display.createFittedContainer(itemSelectMenu.screen, "Throw", aimables);

        Function<Item, UIEventResponse> function = (item) ->{
            Display.getRootMenu().startSelecting(Display.getRootMenu().new AimSelector((Aimable)item));
            return UIEventResponse.processed();
        };
        
        Display.populateContainer(container, function, aimables);

        itemSelectMenu.screen.addComponent(container);

        return itemSelectMenu;
    }
    
    public static ItemSelectMenu createInventoryMenu(PlayerEntity playerEntity){
        ItemSelectMenu itemSelectMenu = new ItemSelectMenu();
        itemSelectMenu.entity = playerEntity;
        itemSelectMenu.type = Type.INVENTORY;
        Set<ItemStack> stacks = playerEntity.getStacks();
        Container container = Display.createFittedContainer(itemSelectMenu.screen, "Inventory", stacks);
        Function <ItemStack, UIEventResponse> function = (stack) ->{
            Display.setMenu(new ItemContextMenu(stack.getItem(), playerEntity));
            return UIEventResponse.processed();
        };
        Display.populateContainer(container, function, stacks);
        itemSelectMenu.screen.addComponent(container);
        return itemSelectMenu;
    }
    
    public static ItemSelectMenu createUpgradeMenu(Upgrader upgrader, HasInventory hasInventory){
        ItemSelectMenu itemSelectMenu = new ItemSelectMenu();
        itemSelectMenu.upgrader = upgrader;
        itemSelectMenu.inventories = new HasInventory[]{hasInventory};
        itemSelectMenu.type = Type.UPGRADE;
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
            Display.setAndForgetMenus(Display.getRootMenu());
            return UIEventResponse.processed();
        };
        Display.populateContainer(container, function, list);
        itemSelectMenu.screen.addComponent(container);
        return itemSelectMenu;
    }

    public static Menu createItemSelectMenu(ItemSlot itemSlot, Entity entity) {

        ItemSelectMenu itemSelectMenu = new ItemSelectMenu(false);
        itemSelectMenu.type = Type.ITEM;
        itemSelectMenu.itemSlot = itemSlot;
        itemSelectMenu.entity = entity;
        
        ArrayList<Item> items = new ArrayList<>();
        if (entity instanceof HasInventory hasInventory){
            items.addAll(hasInventory.getInventory());
        }

        Container container = Display.createFittedContainer(itemSelectMenu.screen, "Select", items);
        
        Item nothing = new Item();

        nothing.setName("Nothing");
        
        Function<Item, UIEventResponse> nothingFunction = nonItem ->{
            try {
                if (entity instanceof HasInventory hasInventory){
                    hasInventory.addItemToInventory(itemSlot.setEquippedItem(null));
                } else {
                    itemSlot.setEquippedItem(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Display.revertMenu();
            return UIEventResponse.processed();
        };

        Display.populateContainer(container, nothingFunction, nothing);

        Function<Item, UIEventResponse> itemFunction = new Function<Item,UIEventResponse>() {
            @Override
            public UIEventResponse apply(Item item){
                try {
                    if (entity instanceof HasInventory hasInventory){
                        hasInventory.removeItemFromInventory(item);
                        hasInventory.addItemToInventory(itemSlot.setEquippedItem(item));
                    } else {
                        itemSlot.setEquippedItem(item);
                    }
                } catch (Exception e) {
                    Display.log(e.getMessage());
                }
                Display.revertMenu();
                return UIEventResponse.processed();
            }
        };

        Display.populateContainer(container, itemFunction, items);

        itemSelectMenu.screen.addComponent(container);

        return itemSelectMenu;
    }

    private enum Type{
        DROP,
        PICKUP,
        INVENTORY_TRANSFER,
        CONSUMABLE,
        WEAPON,
        ARMOR,
        THROW,
        INVENTORY,
        UPGRADE,
        ITEM;
    }

    @Override
    public Menu refresh() {
        switch (this.type) {
            case ARMOR:
                return ItemSelectMenu.createArmorSelectMenu(this.armorSlot, this.entity);
            case CONSUMABLE:
                return ItemSelectMenu.createConsumableSelectMenu(this.entity);
            case DROP:
                return ItemSelectMenu.createDropMenu(this.space, this.inventories[0]);
            case INVENTORY:
                return ItemSelectMenu.createInventoryMenu((PlayerEntity)this.entity);
            case INVENTORY_TRANSFER:
                return ItemSelectMenu.createInventoryTransferMenu(this.inventories[0],this.inventories[1]);
            case ITEM:
                return ItemSelectMenu.createItemSelectMenu(this.itemSlot,this.entity);
            case PICKUP:
                return ItemSelectMenu.createPickupMenu(this.space, this.inventories[0]);
            case THROW:
                return ItemSelectMenu.createThrowMenu(this.inventories[0]);
            case UPGRADE:
                return ItemSelectMenu.createUpgradeMenu(this.upgrader, this.inventories[0]);
            case WEAPON:
                return ItemSelectMenu.createWeaponSelectMenu(this.weaponSlot, this.entity);
            default:
                return new ItemSelectMenu();
        }
    }

}
