package game.gameobjects.floors;

import java.util.Collection;

import game.gameobjects.Space;

@FunctionalInterface
public interface SpaceCollectorStrategy {
    public Collection<Space> collectSpaces(Floor floor);
}
