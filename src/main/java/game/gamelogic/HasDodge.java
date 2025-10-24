package game.gamelogic;

public interface HasDodge extends DodgeModifier {
    public int getDodge();

    @Override
    default int modifyDodge(int dodge){
        return dodge + getDodge();
    }
}
