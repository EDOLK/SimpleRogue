package game.display;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.Dungeon;
import game.gamelogic.HasName;
import game.gamelogic.interactions.HasInteractions;
import game.gamelogic.interactions.Interaction;
import game.gamelogic.interactions.InteractionResult;
import game.gameobjects.entities.Entity;

public class UseMenu extends Menu {

    private Entity interactor;
    private Collection<HasInteractions> interactables;

    public UseMenu(Entity interactor, Collection<HasInteractions> interactables) {
        this.interactor = interactor;
        this.interactables = interactables;
        Display.populateMenu(
            this,
            (obj) -> {
                if (obj instanceof HasInteractions hasInteractions) {
                    Collection<Interaction> interactions = hasInteractions.getInteractions().stream()
                        .filter((i) -> i.isAvailable(interactor))
                        .collect(Collectors.toList());
                    if (interactions.size() > 1) {
                        Display.setMenu(new UseSubMenu(interactor, interactions));
                    } else {
                        Optional<Interaction> first = interactions.stream().findFirst();
                        if (first.isPresent() && !first.get().isDisabled(interactor)) {
                            InteractionResult result = first.get().doInteract(interactor);
                            if (result.revertMenu()){
                                Display.revertMenu();
                            }
                            if (result.timeTaken() > 0) {
                                Dungeon.update(result.timeTaken());
                                Display.update();
                            }
                        }
                    }
                }
                return UIEventResponse.processed();
            },
            "Use What?",
            interactables.stream()
                .map((i) -> i instanceof HasName hn ? hn : (HasName)() -> i.toString())
                .collect(Collectors.toList())
        );
    }

    @Override
    public Menu refresh() {
        return new UseMenu(interactor, interactables);
    }

}
