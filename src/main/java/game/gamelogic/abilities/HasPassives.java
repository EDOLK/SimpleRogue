package game.gamelogic.abilities;

import java.util.List;
import java.util.Optional;

public interface HasPassives {
    public List<Passive> getPassives();
    public boolean addPassive(Passive passive);
    public boolean removePassive(Passive passive);
    default <T> Optional<T> getPassiveByClass(Class<T> clazz){
        return getPassives().stream().filter(clazz::isInstance).map(clazz::cast).findFirst();
    }
}
