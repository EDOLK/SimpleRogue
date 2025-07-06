package game.gamelogic.floorinteraction;

import game.display.Cursor;
import game.gameobjects.Space;

public interface SimpleSelector extends Selector {

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
