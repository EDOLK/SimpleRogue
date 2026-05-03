package game.display;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.App;
import game.gamelogic.Consumable;
import game.gamelogic.Examinable;
import game.gamelogic.HasInventory;
import game.gamelogic.Upgradable;
import game.gamelogic.Upgrader;
import game.gamelogic.floorinteraction.AimSelector;
import game.gameobjects.ItemStack;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.Item;
import game.gameobjects.slots.ItemSlot;

public class ItemSelectMenu extends Menu{

    public ItemSelectMenu(){
        super();
    }

    public static ItemSelectMenu createDropMenu(Space space, HasInventory dropper){
        ItemSelectMenu m = new ItemSelectMenu(){
            @Override
            public Menu refresh() {
                return createDropMenu(space, dropper);
            }
        };
        Display.populateMenu(m, (stack) -> {
            Item item = stack.getItem();
            dropper.removeItemFromInventory(item);
            space.addItem(item);
            Display.replaceMenu(m.refresh());
            return UIEventResponse.processed();
        }, "Dropping", dropper.getStacks());
        return m;
    }

    public static ItemSelectMenu createPickupMenu(Space space, HasInventory pickUpper){
        ItemSelectMenu m = new ItemSelectMenu(){
            @Override
            public Menu refresh() {
                return createPickupMenu(space, pickUpper);
            }
        };
        Display.populateMenu(m, (stack) -> {
            Item item = stack.getItem();
            if (pickUpper.addItemToInventory(item)){
                space.remove(item);
                Display.log("Picked up the " + item.getName() + ".");
            } else {
                Display.log("The " + item.getName() + " is too heavy.");
            }
            Display.replaceMenu(m.refresh());
            return UIEventResponse.processed();
        }, "Pickup", ItemStack.toItemStackSet(space.getItems()));
        return m;
    }

    public static ItemSelectMenu createInventoryTransferMenu(HasInventory fromInventory, HasInventory toInventory){
        ItemSelectMenu m = new ItemSelectMenu(){
            @Override
            public Menu refresh() {
                return ItemSelectMenu.createInventoryTransferMenu(fromInventory, toInventory);
            }
        };
        Display.populateMenu(
            m,
            (stack) -> {
                Item item = stack.getItem();
                if (toInventory.addItemToInventory(item)){
                    fromInventory.removeItemFromInventory(item);
                    Display.log("Took the " + item.getName() + ".");
                } else {
                    Display.log("The " + item.getName() + " is too heavy.");
                }
                Display.replaceMenu(m.refresh());
                return UIEventResponse.processed();
            },
            fromInventory instanceof Examinable examinable ? examinable.getName() : "Pickup",
            ItemStack.toItemStackSet(fromInventory.getInventory())
        );
        return m;
    }

    public static ItemSelectMenu createConsumableSelectMenu(Entity consumer){
        ItemSelectMenu m = new ItemSelectMenu(){
            @Override
            public Menu refresh() {
                return createConsumableSelectMenu(consumer);
            }
        };
        Display.populateMenu(
            m, 
            (stack) -> {
                Item i = stack.getItem();
                if (i instanceof Consumable c){
                    if (c.consume(consumer) && consumer instanceof HasInventory hasInventory){
                        hasInventory.removeItemFromInventory(i);
                    }
                }
                Display.revertMenu();
                return UIEventResponse.processed();
            },
            "Consume",
            consumer instanceof HasInventory hasInventory ?
                ItemStack.toItemStackSet(
                    hasInventory.getInventory().stream()
                        .filter(i -> i instanceof Consumable)
                        .collect(Collectors.toList())
                )
            : new HashSet<ItemStack>()
        );
        return m;
    }

    private static class Nothing extends Item{

        @Override
        public String getName() {
            return "Nothing";
        }

    }

    public static ItemSelectMenu createItemSlotMenu(ItemSlot slot, Entity entity){
        ItemSelectMenu menu = new ItemSelectMenu(){
            @Override
            public Menu refresh() {
                return createItemSlotMenu(slot, entity);
            }
        };
        Display.populateMenu(
            menu,
            (stack) -> {
                Item newItem = stack.getItem();
                newItem = newItem == null || newItem instanceof Nothing ? null : newItem;
                Item prevItem = slot.setItem(newItem);
                if (entity instanceof HasInventory hi) {
                    if (!hi.addItemToInventory(prevItem)){
                        entity.getSpace().addItem(prevItem);
                    }
                    hi.removeItemFromInventory(newItem);
                }
                Display.revertMenu();
                return UIEventResponse.processed();
            },
            "Select",
            ItemStack.toItemStackSet(
                entity instanceof HasInventory hi ?
                App.concatStreams(Stream.of(new Nothing()), hi.getInventory().stream().filter(slot::isValid)).collect(Collectors.toList()) :
                List.of(new Nothing())
            )
        );
        return menu;

    }

    public static ItemSelectMenu createThrowMenu(HasInventory hasInventory){
        ItemSelectMenu menu = new ItemSelectMenu(){
            @Override
            public Menu refresh() {
                return createThrowMenu(hasInventory);
            }
        };
        Display.populateMenu(
            menu,
            (stack) -> {
                if (hasInventory instanceof Entity entity) {
                    Item item = stack.getItem();
                    Display.getRootMenu().startSelecting(new AimSelector(item, entity));
                    return UIEventResponse.processed();
                }
                return UIEventResponse.pass();
            },
            "Throw",
            ItemStack.toItemStackSet(hasInventory.getInventory())
        );
        return menu;
    }
    
    public static ItemSelectMenu createInventoryMenu(PlayerEntity playerEntity){
        ItemSelectMenu menu = new ItemSelectMenu(){
            @Override
            public Menu refresh() {
                return createInventoryMenu(playerEntity);
            }
        };
        Display.populateMenu(
            menu,
            (stack) -> {
                Display.setMenu(new ItemContextMenu(stack.getItem(), playerEntity));
                return UIEventResponse.processed();
            },
            "Inventory",
            ItemStack.toItemStackSet(playerEntity.getInventory())
        );
        return menu;
    }

    public static ItemSelectMenu createUpgradeMenu(Upgrader upgrader, HasInventory hasInventory){
        ItemSelectMenu menu = new ItemSelectMenu(){
            @Override
            public Menu refresh() {
                return createUpgradeMenu(upgrader, hasInventory);
            }
        };
        Display.populateMenu(
            menu,
            (stack) -> {
                Item item = stack.getItem();
                if (item instanceof Upgradable upgradable){
                    if (upgradable.upgrade(upgrader)){
                        hasInventory.removeItemFromInventory((Item)upgrader);
                    }
                }
                Display.setAndForgetMenus(Display.getRootMenu());
                return UIEventResponse.processed();
            },
            "Upgrade",
            ItemStack.toItemStackSet(hasInventory.getInventory().stream().filter(i->i instanceof Upgradable upgradable && upgradable.canUpgrade(upgrader)).collect(Collectors.toList()))
        );
        return menu;
    }


}
