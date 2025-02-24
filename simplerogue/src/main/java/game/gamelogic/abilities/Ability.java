package game.gamelogic.abilities;

import game.gamelogic.HasName;

public interface Ability extends HasName{
    public void activate();
    public boolean isEnabled();
}
