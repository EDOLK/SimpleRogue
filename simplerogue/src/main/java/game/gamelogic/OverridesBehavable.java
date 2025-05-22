package game.gamelogic;

import game.gamelogic.behavior.Behavable;

public interface OverridesBehavable {

    public int overrideBehave(Behavable behavable);

    public boolean overrideIsActive(Behavable behavable);

}
