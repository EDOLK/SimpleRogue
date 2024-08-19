package game.gameobjects.entities;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.display.Display;
import game.display.ItemSelectMenu;
import game.floorgeneration.Pool;
import game.floorgeneration.ItemFactory.ItemIdentifier;
import game.gamelogic.HasDrops;
import game.gamelogic.HasInventory;
import game.gamelogic.Interactable;
import game.gameobjects.items.Item;

public class Chest extends Entity implements HasInventory, Interactable{

    private List<Item> inventory = new ArrayList<Item>();
    
    public Chest(Pool<ItemIdentifier> treasurePool, int points){
        super(TileColor.transparent(), TileColor.create(230, 188, 64, 255), 'c');
        setMaxHP(5);
        setHP(5);
        setWeight(5);
        setName("Chest");
        setTileName("Chest");
        setDescription("A chest. It probably has things in it.");
        setCorpse(null);
        HasDrops hasDrops = new HasDrops() {

            @Override
            public Pool<ItemIdentifier> getItemPool() {
                return treasurePool;
            }

            @Override
            public int getDropPoints() {
                return points;
            }

            @Override
            public void setDropPoints(int points) {
                return;
            }
            
        };
        List<Item> itemList = hasDrops.generateDrops();
        for (Item item : itemList) {
            addItemToInventory(item);
        }
    }

    @Override
    public void onInteract(Entity interactor) {
        if (interactor instanceof HasInventory interactorWithInventory){
            Display.setMenu(ItemSelectMenu.createInventoryTransferMenu(this, interactorWithInventory));
        }
    }

    @Override
    public List<Item> getInventory() {
        return inventory;
    }

    @Override
    public int getMaxWeight() {
        return 999;
    }

    @Override
    public void defaultInteraction(Entity interactor) {
        Display.log("There is a " + getName() + " in the way");
    }
    
}
