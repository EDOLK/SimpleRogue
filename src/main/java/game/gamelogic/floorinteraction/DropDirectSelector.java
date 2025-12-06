package game.gamelogic.floorinteraction;

import game.gamelogic.HasInventory;
import game.gameobjects.Space;
import game.gameobjects.items.Item;

public class DropDirectSelector implements SimpleSelector {

    private Item item;
    private HasInventory dropper;

    public DropDirectSelector(Item item, HasInventory dropper) {
        this.item = item;
        this.dropper = dropper;
    }

    @Override
    public SelectionResult simpleSelect(Space space) {
        space.addItem(item);
        dropper.removeItemFromInventory(item);
        return new SelectionResult(true, 0);
    }

}
