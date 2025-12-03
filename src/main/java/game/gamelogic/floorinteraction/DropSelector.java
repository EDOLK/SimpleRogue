package game.gamelogic.floorinteraction;

import game.display.Display;
import game.display.ItemSelectMenu;
import game.gamelogic.HasInventory;
import game.gameobjects.Space;

public class DropSelector implements SimpleSelector{

    private HasInventory dropper;

    public DropSelector(HasInventory dropper) {
        this.dropper = dropper;
    }

    @Override
    public SelectionResult simpleSelect(Space space) {
        Display.setMenu(ItemSelectMenu.createDropMenu(space, dropper));
        return new SelectionResult(true, 0);
    }

}
