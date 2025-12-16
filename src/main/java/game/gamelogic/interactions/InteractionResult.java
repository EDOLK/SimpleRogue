package game.gamelogic.interactions;

public class InteractionResult {

    private int timeTaken = 0;

    private boolean revertMenu = false;

    public int getTimeTaken() {
        return timeTaken;
    }

    public boolean isRevertMenu() {
        return revertMenu;
    }

    public static InteractionResult create(){
        return new InteractionResult();
    }

    public InteractionResult withTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
        return this;
    }

    public InteractionResult withRevertMenu() {
        this.revertMenu = true;
        return this;
    }

}

