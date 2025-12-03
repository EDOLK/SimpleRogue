package game.gamelogic.floorinteraction;

import game.display.Display;
import game.display.ItemSelectMenu;
import game.gamelogic.HasInventory;
import game.gameobjects.Space;

public class GetSelector implements SimpleSelector{

    private HasInventory inventoryHaver;

    public GetSelector(HasInventory inventoryHaver){
        this.inventoryHaver = inventoryHaver;
    }

    @Override
    public SelectionResult simpleSelect(Space space) {
        Display.setMenu(ItemSelectMenu.createPickupMenu(space, inventoryHaver));
        return new SelectionResult(true, 0);
    }

}
