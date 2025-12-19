package game.gameobjects.floors;

import java.util.Collection;
import java.util.Optional;

import game.gameobjects.Space;
import game.gameobjects.entities.PlayerEntity;

public interface Floor {
    public int getSizeY();
    public int getSizeX();
    public int clampX(int x);
    public int clampY(int y);
    public Space getSpace(int x, int y);
    public void update(int time);
    public void doLight();
    public PlayerEntity getPlayer();
    default Space getClampedSpace(int x, int y){
        return getSpace(clampX(x),clampY(y));
    }
    default void update(){
        update(100);
    };
    default Optional<Space> getSpace(SpaceGetterStrategy strategy){
        return strategy.getSpace(this);
    };
    default Optional<Collection<Space>> getSpaces(SpaceCollectorStrategy strategy){
        return strategy.collectSpaces(this);
    };
}
