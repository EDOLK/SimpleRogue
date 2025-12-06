package game.display;

import java.util.Collection;
import java.util.List;

import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.Dungeon;
import game.gamelogic.interactions.Interaction;
import game.gamelogic.interactions.InteractionResult;
import game.gameobjects.entities.Entity;
import kotlin.Pair;

public class UseSubMenu extends Menu {

    private Entity interactor;
    private Collection<Interaction> interactions;

    public UseSubMenu(Entity interactor, Collection<Interaction> interactions) {
        this.interactor = interactor;
        this.interactions = interactions;
        // interactions size is guaranteed to be > 1
        List<Pair<Button,Interaction>> pairs = Display.populateMenu(
            this,
            (interaction) -> {
                InteractionResult result = interaction.doInteract(interactor);
                if (result.revertMenu()){
                    Display.revertMenu();
                }
                if (result.timeTaken() > 0) {
                    Dungeon.update(result.timeTaken());
                    Display.update();
                }
                return UIEventResponse.processed();
            },
            "Use How?",
            interactions
        );
        for (Pair<Button,Interaction> pair : pairs) {
            pair.getFirst().setDisabled(pair.getSecond().isDisabled(interactor));
        }
    }

    @Override
    public Menu refresh() {
        return new UseSubMenu(interactor, interactions);
    }

}

