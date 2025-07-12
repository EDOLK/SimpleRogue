package game.gameobjects.entities.props;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.display.Display;
import game.display.ItemSelectMenu;
import game.gamelogic.Attribute;
import game.gamelogic.HasInventory;
import game.gamelogic.Interactable;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.Item;

public abstract class ContainerProp extends Entity implements Interactable, HasInventory{

    private List<Item> inventory = new ArrayList<Item>();

    public ContainerProp(TileColor bGColor, TileColor fGColor, char character) {
        super(bGColor, fGColor, character);
    }

    public ContainerProp() {
    }

    @Override
    public List<Item> getInventory() {
        return this.inventory;
    }

    @Override
    public Item getCorpse() {
        return null;
    }

    @Override
    public int getHardWeightLimit() {
        return 999;
    }

    @Override
    public int defaultInteraction(Entity interactor){
        int str = 0;
        str += Attribute.getAttribute(Attribute.STRENGTH, interactor);
        if ((str*5) >= this.getWeight()) {
            int xOffset = this.getX() - interactor.getX();
            int yOffset = this.getY() - interactor.getY();
            Space nextSpace = Dungeon.getCurrentFloor().getSpace(
                this.getX() + xOffset,
                this.getY() + yOffset
            );
            if (nextSpace.isOccupied()) {
                if (interactor instanceof PlayerEntity) {
                    Display.log("Something's in the way.");
                }
            } else {
                Space oldSpace = this.getSpace();
                Space.moveEntity(this, nextSpace);
                Space.moveEntity(interactor, oldSpace);
                return interactor.getTimeToMove()*2;
            }
        }
        return interactor.getTimeToWait();
    }

    @Override
    public void onInteract(Entity interactor) {
        if (interactor instanceof HasInventory interactorWithInventory){
            Display.setMenu(ItemSelectMenu.createInventoryTransferMenu(this, interactorWithInventory));
        }
    }

    @Override
    public String getDeathMessage() {
        return "The " + getName() + " breaks.";
    }

}
