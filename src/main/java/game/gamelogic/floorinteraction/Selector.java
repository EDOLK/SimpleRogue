package game.gamelogic.floorinteraction;

import game.display.Cursor;
import game.gameobjects.Space;

public interface Selector {

    // If returns true, leave selection mode and return to ingame mode. otherwise stay in selection mode.
    public boolean select(Cursor cursor);

    public boolean canMove(Cursor cursor, Space toSpace);

}
