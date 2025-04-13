package game.gamelogic.behavior;

public interface Behavable {

    public void behave();

    public boolean isActive();

    default int getDelay(){
        return 0;
    };

}
