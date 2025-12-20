package game.gameobjects.floors;

import java.util.Optional;

import game.gameobjects.Space;

@FunctionalInterface
public interface SpaceGetterStrategy {
    public Optional<Space> getSpace(Floor floor);
}
