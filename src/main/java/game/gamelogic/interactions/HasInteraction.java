package game.gamelogic.interactions;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface HasInteraction extends HasInteractions{
    public Interaction getInteraction();
    @Override
    default Collection<Interaction> getInteractions() {
        Interaction interaction = getInteraction();
        if (interaction != null)
            return List.of(interaction);
        return Collections.emptyList();
    }
}
