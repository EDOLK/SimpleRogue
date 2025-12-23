package game.gamelogic;

public interface HasAffiliation {
    default Affiliation getAffiliation(){
        return Affiliation.NEUTRAL;
    }
}
