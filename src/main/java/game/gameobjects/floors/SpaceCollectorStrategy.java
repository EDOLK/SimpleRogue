package game.gameobjects.floors;

import java.util.Collection;
import java.util.Optional;

import game.gameobjects.Space;

@FunctionalInterface
public interface SpaceCollectorStrategy {
    public Optional<Collection<Space>> collectSpaces(Floor floor);
}
