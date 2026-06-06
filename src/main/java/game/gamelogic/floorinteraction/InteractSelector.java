package game.gamelogic.floorinteraction;

import java.util.Collection;
import java.util.Optional;

import game.display.Display;
import game.display.UseMenu;
import game.display.UseSubMenu;
import game.display.animations.Animations;
import game.display.animations.WaitAnimation;
import game.gamelogic.SelfAware;
import game.gamelogic.interactions.HasInteractions;
import game.gamelogic.interactions.Interaction;
import game.gamelogic.interactions.InteractionResult;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;

public class InteractSelector implements SimpleSelector{

    private Entity entity;

    public InteractSelector(Entity entity) {
        this.entity = entity;
    }

    @Override
    public SelectionResult simpleSelect(Space space) {
        Collection<HasInteractions> his = HasInteractions.gather(space);
        if (his.size() > 1) {
            Display.setMenu(new UseMenu(entity, his));
        } else {
            Optional<HasInteractions> first = his.stream().findFirst();
            if (first.isPresent()) {
                Collection<Interaction> interactions = first.get().getInteractions();
                if (interactions.size() > 1) {
                    Display.setMenu(new UseSubMenu(entity, interactions));
                } else {
                    Optional<Interaction> fInter = interactions.stream().findFirst();
                    if (fInter.isPresent()) {
                        InteractionResult result = fInter.get().doInteract(entity);
                        if (first.get() instanceof SelfAware sa) {
                            Animations.enqueue(new WaitAnimation(sa));
                        }
                        return new SelectionResult(true, result.getTimeTaken());
                    }
                }
            }
        }
        return new SelectionResult(true, 0);
    }

}
