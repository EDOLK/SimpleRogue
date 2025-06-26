package game.gamelogic;

import game.gameobjects.statuses.Status;

public interface HasStatusVulns{
    public boolean isVulnerable(Status status);
}
