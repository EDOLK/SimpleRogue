package game.gamelogic.floorinteraction;

import game.display.Cursor;
import game.gameobjects.Space;

public interface Selector {
    public boolean select(Cursor cursor);
    public boolean canMove(Cursor cursor, Space toSpace);
}
