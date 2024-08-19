package game.gameobjects.statuses;

import java.util.List;

public interface Seperate{
    public void onStack(Status SameStatus);
    public Status validateSameness(List<Status> Statuses);
}
