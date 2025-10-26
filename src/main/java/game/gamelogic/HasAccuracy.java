package game.gamelogic;

public interface HasAccuracy extends AccuracyModifier {
    public int getAccuracy();

    @Override
    default int modifyAccuracy(int accuracy) {
        return accuracy + getAccuracy();
    }

}
