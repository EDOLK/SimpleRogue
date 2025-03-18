package game.gameobjects.statuses;

import java.util.List;

public interface Seperate{
    public void onStack(Status sameStatus);
    public Status validateSameness(List<Status> statuses);
}
