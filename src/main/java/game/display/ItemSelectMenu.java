package game.display;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static ItemSelectMenu createWeaponSelectMenu(WeaponSlot weaponSlot, Entity entity){
        ItemSelectMenu itemSelectMenu = new ItemSelectMenu(){
            @Override
            public Menu refresh() {
                return createWeaponSelectMenu(weaponSlot, entity);
            }
        };
        Display.populateMenu(
            itemSelectMenu,
            (stack) -> {
                Item item = stack.getItem();
                if (item instanceof Weapon newWeapon) {
                    if (entity instanceof HasInventory hasInventory){
                        hasInventory.removeItemFromInventory(newWeapon);
                        hasInventory.addItemToInventory(weaponSlot.setEquippedWeapon(newWeapon));
                    } else {
                        weaponSlot.setEquippedWeapon(newWeapon);
                    }
                    Display.revertMenu();
                    return UIEventResponse.processed();
                } else if (item instanceof Nothing){
                    if (entity instanceof HasInventory hasInventory){
                        hasInventory.addItemToInventory(weaponSlot.setEquippedWeapon(null));
                    } else {
                        weaponSlot.setEquippedWeapon(null);
                    }
                    Display.revertMenu();
                    return UIEventResponse.processed();
                }
                return UIEventResponse.pass();
            },
            "Select Weapon",
            ItemStack.toItemStackSet(
                Stream.concat(
                    List.of(new Nothing()).stream().map(n->(Item)n),
                    entity instanceof HasInventory hi ? hi.getInventory().stream().filter(i->i instanceof Weapon) : List.of().stream().map(i->(Item)i)
                ).collect(Collectors.toList())
            )
        );
        return itemSelectMenu;
    }

    public static ItemSelectMenu createArmorSelectMenu(ArmorSlot armorSlot, Entity entity){
        ItemSelectMenu menu = new ItemSelectMenu(){
            @Override
            public Menu refresh() {
                return createArmorSelectMenu(armorSlot, entity);
            }
        };
        Display.populateMenu(
            menu, 
            (stack) -> {
                Item item = stack.getItem();
                if (item instanceof Armor armor) {
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
                } else if (item instanceof Nothing){
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
                }
                return UIEventResponse.pass();
            }, 
            "Select Armor",
            ItemStack.toItemStackSet(
                Stream.concat(
                    List.of(new Nothing()).stream().map(n->(Item)n),
                    entity instanceof HasInventory hi ? hi.getInventory().stream().filter(i->i instanceof Armor armor && armor.getArmorType() == armorSlot.getType()) : List.of().stream().map(i->(Item)i)
                ).collect(Collectors.toList())
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
                    Display.getRootMenu().startSelecting(Display.getRootMenu().new AimSelector(item, entity));
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

    public static Menu createItemSelectMenu(ItemSlot itemSlot, Entity entity){
        ItemSelectMenu menu = new ItemSelectMenu(){
            @Override
            public Menu refresh() {
                return createItemSelectMenu(itemSlot, entity);
            }
        };
        Display.populateMenu(
            menu,
            (stack) -> {
                Item item = stack.getItem();
                if (item instanceof Nothing) {
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
                }
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
            },
            "Select",
            ItemStack.toItemStackSet(
                Stream.concat(
                    List.of(new Nothing()).stream(),
                    entity instanceof HasInventory hi ? hi.getInventory().stream() : List.of().stream().map(o->(Item)o)
                ).collect(Collectors.toList())
            )
        );
        return menu;
    }

}
