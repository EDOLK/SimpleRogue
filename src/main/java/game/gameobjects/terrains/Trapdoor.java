package game.gameobjects.terrains;

import org.hexworks.zircon.api.color.TileColor;

import game.display.Display;
import game.gamelogic.HasInventory;
import game.gamelogic.interactions.Interaction;
import game.gamelogic.interactions.InteractionResult;
import game.gameobjects.Space;
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
    public Interaction getInteraction(){
        return new Interaction.Builder()
            .withOnInteract((interactor) -> {
                if (interactor instanceof HasInventory hasInventory) {
                    for (Item item : hasInventory.getInventory()) {
                        if (item == keyItem) {
                            Display.log("You unlock the trapdoor.");
                            hasInventory.removeItemFromInventory(keyItem);
                            Space space = getSpace();
                            space.remove(this);
                            space.addTerrain(staircase);
                            return InteractionResult.create()
                                .withRevertMenu();
                        }
                    }
                }
                Display.log("The trapdoor is locked.");
                return InteractionResult.create()
                    .withRevertMenu();
            })
            .withName("Unlock")
            .build();
    }

    
}
