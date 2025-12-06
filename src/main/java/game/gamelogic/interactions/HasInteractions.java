package game.gamelogic.interactions;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import game.App;
import game.CheckConditions;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;

public interface HasInteractions {
    public Collection<Interaction> getInteractions();

    public static Collection<HasInteractions> gather(Space space){
        return App.concatStreams(
            toInteractable(space.isOccupied() ? Stream.of(space.getOccupant()) : Stream.empty()),
            toInteractable(space.getItems().stream()),
            toInteractable(space.getTerrains().stream())
        ).collect(Collectors.toList());
    }

    private static Stream<HasInteractions> toInteractable(Stream<?> stream){
        return stream
            .filter(o -> o instanceof HasInteractions)
            .map(o -> (HasInteractions)o);
    }

    public static Collection<HasInteractions> gather(Entity entity, boolean include){
        return App.recursiveCheck(
            entity,
            CheckConditions.none().withInventory(true).withOffHand(true),
            obj -> obj instanceof HasInteractions ha ? Optional.of(ha) : Optional.empty()
        ).stream().filter(r -> include ? true : r != entity).collect(Collectors.toList());
    }

}
