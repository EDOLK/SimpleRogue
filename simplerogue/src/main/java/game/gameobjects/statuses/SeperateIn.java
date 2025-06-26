package game.gameobjects.statuses;

/** 
 * Stops the status that implements this interface from being added to an entity which contains atleast one status that match the filter.
 * */
public interface SeperateIn{

    public void onStackIn(Status sameStatus);

    public boolean validateSamenessIn(Status status);

}
