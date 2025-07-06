package game.gamelogic;

import game.gameobjects.Space;

public interface SelfAware{
    public Space getSpace();
    public void setSpace(Space space);
    default int getX(){
        return getSpace().getX();
    }
    default int getY(){
        return getSpace().getY();
    }
}
