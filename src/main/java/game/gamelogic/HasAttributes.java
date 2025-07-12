package game.gamelogic;

public interface HasAttributes {

    public AttributeMap getAttributeMap();

    default int getAttribute(Attribute attribute){
        return getAttributeMap().get(attribute);
    }

    default void setAttribute(Attribute attribute, int amount){
        getAttributeMap().set(attribute,amount);
    }

    default void incrementAttribute(Attribute attribute){
        getAttributeMap().increment(attribute);
    }

    default void decrementAttribute(Attribute attribute){
        getAttributeMap().decrement(attribute);
    }

    default int getAttributePoints(){
        return -1;
    }

    default void setAttributePoints(int att){
        
    }

}
