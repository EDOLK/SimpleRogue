package game.gameobjects.floors;

import java.util.Collection;
import java.util.Optional;

import game.gameobjects.Space;
import game.gameobjects.entities.PlayerEntity;

public interface Floor {

    public Space getSpace(int x, int y);

    public int getSizeY();
    public int getSizeX();

    default int clampX(int x){
        return x = x >= this.getSizeX() ? this.getSizeX()-1 : (x < 0 ? 0 : x);
    }
    default int clampY(int y){
        return y = y >= this.getSizeY() ? this.getSizeY()-1 : (y < 0 ? 0 : y);
    }

    default Space getClampedSpace(int x, int y){
        return getSpace(clampX(x),clampY(y));
    }

    default void update(){
        update(100);
    };

    public void update(int time);

    public PlayerEntity getPlayer();

    default Optional<Space> getSpace(SpaceGetterStrategy strategy){
        return strategy.getSpace(this);
    };

    default Collection<Space> collectSpaces(SpaceCollectorStrategy strategy){
        return strategy.collectSpaces(this);
    };

}
