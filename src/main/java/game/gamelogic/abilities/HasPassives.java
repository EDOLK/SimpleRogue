package game.gamelogic.abilities;

import java.util.List;

public interface HasPassives {
    public List<Passive> getPassives();
    public boolean addPassive(Passive passive);
    public boolean removePassive(Passive passive);
}
