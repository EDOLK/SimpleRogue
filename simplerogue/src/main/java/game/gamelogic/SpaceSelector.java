package game.gamelogic;

import game.display.FloorMenu;
import game.gameobjects.Space;

public interface SpaceSelector {
    public int getRange();
    public boolean activate(Space sourceSpace, Space selectedSpace, FloorMenu menu);
    public boolean isSimple();
}
