package game.gamelogic.interactions;

import game.display.Display;
import game.display.ItemSelectMenu;
import game.gamelogic.HasInventory;
import game.gamelogic.HasName;
import game.gameobjects.entities.Entity;

public class OpenInventoryInteraction implements Interaction {

    private HasInventory inventoryToOpen;

    public OpenInventoryInteraction(HasInventory inventoryToOpen) {
        this.inventoryToOpen = inventoryToOpen;
    }

    @Override
    public String getName() {
        return "Open" + (inventoryToOpen instanceof HasName hn ? " " + hn.getName() : "");
    }

    @Override
    public InteractionResult doInteract(Entity interactor) {
        if (interactor instanceof HasInventory interactorWithInventory){
            Display.setMenu(
                ItemSelectMenu.createInventoryTransferMenu(inventoryToOpen, interactorWithInventory)
            );
        }
        return InteractionResult.create()
            .withTimeTaken(100);
    }

}
