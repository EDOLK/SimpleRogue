package game.gamelogic.floorinteraction;

import game.display.Cursor;
import game.gameobjects.Space;

@FunctionalInterface
public interface SimpleSelector extends Selector {

    // If returns true, leave selection mode and return to ingame mode. otherwise stay in selection mode.
    public boolean simpleSelect(Space space);

    @Override
    default boolean canMove(Cursor cursor, Space toSpace) {
        throw new UnsupportedOperationException("Unimplemented method 'canMove'");
    }
    @Override
    default boolean select(Cursor cursor) {
        return simpleSelect(cursor.getSelectedSpace());
    }
}
