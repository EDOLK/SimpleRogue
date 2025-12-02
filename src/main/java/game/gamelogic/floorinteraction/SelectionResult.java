package game.gamelogic.floorinteraction;

public class SelectionResult {

    private boolean submitted = false;
    private int timeTaken = 0;

    public SelectionResult(boolean submitted, int timeTaken) {
        this.submitted = submitted;
        this.timeTaken = timeTaken;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

}
