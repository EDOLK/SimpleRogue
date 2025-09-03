package game.gamelogic;

public interface HasDodge extends ModifiesDodge {
    public int getDodge();

    @Override
    default int modifyDodge(int dodge){
        return dodge + getDodge();
    }
}
