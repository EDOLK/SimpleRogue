package game.gamelogic;

@FunctionalInterface
public interface AccuracyModifier {
    public int modifyAccuracy(int accuracy);
}
