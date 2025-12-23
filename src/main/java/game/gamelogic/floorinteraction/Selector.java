package game.gamelogic.floorinteraction;

import java.util.List;
import java.util.function.Consumer;

import game.display.Cursor;
import game.gameobjects.Space;

public interface Selector {

    // If returns true, leave selection mode and return to ingame mode. otherwise stay in selection mode.
    public SelectionResult select(Cursor cursor);

    public boolean canMove(Cursor cursor, Space toSpace);

    default void attachHook(Consumer<SelectionResult> hook){
        SelectorHookMap.put(this, hook);
    }

    default void detachHook(Consumer<SelectionResult> hook){
        SelectorHookMap.remove(this, hook);
    }

    default List<Consumer<SelectionResult>> getHooks(){
        return SelectorHookMap.get(this);
    }

}
