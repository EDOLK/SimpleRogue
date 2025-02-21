package game.gamelogic.abilities;

import game.gamelogic.HasName;
import game.gameobjects.entities.Entity;

public interface Ability extends HasName{
    public void activate();
    public boolean isEnabled();
}
