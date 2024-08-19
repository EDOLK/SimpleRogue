package game.gamelogic.behavior;

public interface HasBehavior extends Behavable{

    public Behavior getBehavior();

    public boolean setBehavior(Behavior behavior);
    
    @Override
    default void behave() {
        getBehavior().behave();
    }

    @Override
    default boolean isActive() {
        return getBehavior() != null;
    }

}
