package game.gamelogic.behavior;

public interface HasBehavior extends Behavable{

    public Behavior getBehavior();

    public boolean setBehavior(Behavior behavior);

    default boolean hasBehavior(){
        return getBehavior() != null;
    }
    
    @Override
    default void behave() {
        getBehavior().behave();
    }

    @Override
    default boolean isActive() {
        return hasBehavior();
    }

}
