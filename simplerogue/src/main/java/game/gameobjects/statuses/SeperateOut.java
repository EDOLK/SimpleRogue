package game.gameobjects.statuses;

/** 
 * Stops other statuses that match the filter from being added to the entity which currently holds the status that implements this interface.
 * */
public interface SeperateOut{

    public void onStackOut(Status sameStatus);

    public boolean validateSamenessOut(Status status);

}
