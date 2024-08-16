package game.gamelogic;

import game.gameobjects.entities.Entity;

public interface Scrollable {
    public boolean read(Entity reader);
    public boolean opensMenu();
}
