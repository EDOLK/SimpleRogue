package game.gamelogic;

public interface HasAccuracy extends ModifiesAccuracy {
    public int getAccuracy();

    @Override
    default int modifyAccuracy(int accuracy) {
        return accuracy + getAccuracy();
    }

}
