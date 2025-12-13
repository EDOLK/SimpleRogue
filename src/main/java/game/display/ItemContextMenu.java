package game.display;

import org.hexworks.zircon.api.ComponentDecorations;
import org.hexworks.zircon.api.builder.component.ButtonBuilder;
import org.hexworks.zircon.api.builder.component.HeaderBuilder;
import org.hexworks.zircon.api.builder.component.PanelBuilder;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.Header;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.uievent.ComponentEventType;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.gamelogic.Consumable;
import game.gamelogic.Interactable;
import game.gamelogic.Scrollable;
import game.gamelogic.floorinteraction.AimSelector;
import game.gameobjects.ArmorSlot;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;

public class ItemContextMenu extends Menu{

    private boolean consumable = false;
    private boolean interactable = false;
    private boolean readable = false;
    private boolean weapon = false;
    private boolean armor = false;

    private Item item;
    private PlayerEntity playerEntity;

    public ItemContextMenu(Item item, PlayerEntity playerEntity){
        super();
        this.item = item;
        this.playerEntity = playerEntity;

        int width = item.getName().length() > 8 ? item.getName().length()+5 : 15;
        int height = 10;
        if (item instanceof Interactable){
            height+=2;
            interactable = true;
        }
        if (item instanceof Consumable){
            height+=2;
            consumable = true;
        }
        if (item instanceof Scrollable){
            height+=2;
            readable = true;
        }
        if (item instanceof Weapon){
            height+=2;
            weapon = true;
        }
        if (item instanceof Armor){
            height+=2;
            armor = true;
        }

        Panel itemPanel = PanelBuilder.newBuilder()
            .withDecorations(ComponentDecorations.box(BoxType.SINGLE))
            .withPreferredSize(width, height)
            .withPosition(getScreen().getWidth()/2 - width/2, getScreen().getHeight()/2 - height/2)
            .build();
        
        getScreen().addComponent(itemPanel);

        Tile tile = item.getTile();
        
        screen.draw(tile,Position.topLeftOf(itemPanel).plus(Position.create(1, 1)));
        
        Header nameHeader = HeaderBuilder.newBuilder()
            .withText(item.getName())
            .withPosition(2,0)
            .build();
        
        itemPanel.addComponent(nameHeader);
        
        int pos = 2;

        Button examineButton = ButtonBuilder.newBuilder()
            .withText("Examine")
            .withPosition(2, pos)
            .build();

        examineButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
            Display.setMenu(new ExamineMenu(item));
            return UIEventResponse.processed();
        });

        itemPanel.addComponent(examineButton);
        
        pos += 2;
        
        Button dropButton = ButtonBuilder.newBuilder()
            .withText("Drop")
            .withPosition(2, pos)
            .build();
        
        dropButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
            Display.getRootMenu().startSelecting(new DropDirectSelector(item, playerEntity));
            return UIEventResponse.processed();
        });

        itemPanel.addComponent(dropButton);
        
        pos += 2;

        if (weapon){
            Button equipButton = ButtonBuilder.newBuilder()
                .withText("Equip")
                .withPosition(2, pos)
                .build();
            equipButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
                if (playerEntity.getWeaponSlots().size() > 0){
                    playerEntity.addItemToInventory(playerEntity.getWeaponSlots().get(0).setEquippedWeapon((Weapon)item));
                    playerEntity.removeItemFromInventory(item);
                }
                Display.revertMenu();
                // Display.setMenu(ItemSelectMenu.createInventoryMenu(Dungeon.getCurrentFloor().getPlayer()));
                return UIEventResponse.processed();
            });
            itemPanel.addComponent(equipButton);
            pos+=2;
        }

        if (armor){
            ArmorSlot itemArmorSlot = null;
            for (ArmorSlot armorSlot : playerEntity.getArmorSlots()) {
                if (armorSlot.getType() == ((Armor)item).getArmorType()){
                    itemArmorSlot = armorSlot;
                    break;
                }
            }
            if (itemArmorSlot != null){
                final ArmorSlot thisArmorSlot = itemArmorSlot;
                Button equipButton = ButtonBuilder.newBuilder()
                    .withText("Equip")
                    .withPosition(2, pos)
                    .build();
                equipButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
                    try {
                        playerEntity.addItemToInventory(thisArmorSlot.setEquippedArmor((Armor)item));
                        playerEntity.removeItemFromInventory(item);
                        Display.revertMenu();
                    } catch (Exception e) {
                        Display.log(e.getMessage());
                    }
                    return UIEventResponse.processed();
                });
                itemPanel.addComponent(equipButton);
                pos+=2;
            }
        }
        
        if (consumable){
            Button consumeButton = ButtonBuilder.newBuilder()
                .withText("Consume")
                .withPosition(2, pos)
                .build();
            consumeButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) ->{
                if(((Consumable)item).consume(playerEntity)){
                    playerEntity.removeItemFromInventory(item);
                }
                Display.revertMenu();
                return UIEventResponse.processed();
            });
            itemPanel.addComponent(consumeButton);
            pos+=2;
        }
        
        Button throwButton = ButtonBuilder.newBuilder()
            .withText("Throw")
            .withPosition(2,pos)
            .build();
        throwButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) ->{
            Display.getRootMenu().startSelecting(
                new AimSelector(item, playerEntity)
            );
            return UIEventResponse.processed();
        });
        itemPanel.addComponent(throwButton);
        pos+=2;
        
        if (readable){
            Button readButton = ButtonBuilder.newBuilder()
                .withText("Read")
                .withPosition(2, pos)
                .build();
            readButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) ->{
                if (item instanceof Scrollable readableItem){
                    if (readableItem.read(playerEntity) && !readableItem.opensMenu()){
                        Display.setAndForgetMenus(Display.getRootMenu());
                    }
                }
                return UIEventResponse.processed();
            });
            itemPanel.addComponent(readButton);
            pos+=2;
        }
        
        if (interactable){
            Button interactButton = ButtonBuilder.newBuilder()
                .withText("Interact")
                .withPosition(2, pos)
                .build();
            interactButton.handleComponentEvents(ComponentEventType.ACTIVATED, (event) ->{
                ((Interactable)item).onInteract(playerEntity);
                return UIEventResponse.processed();
            });
            itemPanel.addComponent(interactButton);
            pos+=2;
        }

    }

    @Override
    public Menu refresh() {
        return new ItemContextMenu(this.item, this.playerEntity);
    }

}
