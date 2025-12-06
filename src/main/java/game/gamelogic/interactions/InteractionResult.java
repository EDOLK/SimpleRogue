package game.gamelogic.interactions;

public class InteractionResult {

    private int timeTaken = 0;

    private boolean revertMenu = false;

    public int timeTaken() {
        return timeTaken;
    }

    public boolean revertMenu() {
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

