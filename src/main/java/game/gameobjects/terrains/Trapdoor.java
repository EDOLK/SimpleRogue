package game.gameobjects.terrains;

import org.hexworks.zircon.api.color.TileColor;

import game.display.Display;
import game.gamelogic.HasInventory;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;

public class Trapdoor extends Staircase {

    private Staircase staircase;
    private Item keyItem;

    @Override
    public String getDescription() {
        return "A Trapdoor, it's locked.";
    }

    @Override
    public String getName() {
        return "Trapdoor";
    }

    public Trapdoor(Staircase staircase, Item keyItem) {
        super();
        this.staircase = staircase;
        this.keyItem = keyItem;
        setFgColor(TileColor.create(125, 125, 125, 255));
    }

    @Override
    public void onInteract(Entity interactor) {
        boolean opening = false;
        HasInventory inventoryHaver = null;
        if (interactor instanceof HasInventory hasInventory) {
            for (Item item : hasInventory.getInventory()) {
                if (item == keyItem) {
                    opening = true;
                    inventoryHaver = hasInventory;
                }
            }
        }
        if (opening) {
            Display.log("You unlock the trapdoor.");
            inventoryHaver.removeItemFromInventory(keyItem);
            Space space = getSpace();
            space.remove(this);
            space.addTerrain(staircase);
            return;
        } else {
            Display.log("The trapdoor is locked.");
            return;
        }
    }
}
