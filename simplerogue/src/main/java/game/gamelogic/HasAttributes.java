package game.gamelogic;

import game.gamelogic.AttributeMap.Attribute;

public interface HasAttributes {

    public AttributeMap getMap();

    default int getAttribute(Attribute attribute){
        return getMap().get(attribute);
    }

    default void setAttribute(Attribute attribute, int amount){
        getMap().set(attribute,amount);
    }

    default int getAttributePoints(){
        return -1;
    }

    default void setAttributePoints(int att){
        
    }

}
