package game.gamelogic.abilities;

import java.util.List;
import java.util.Optional;

public interface HasAbilities {
    public List<Ability> getAbilities();
    public boolean addAbility(Ability ability);
    public boolean removeAbility(Ability ability);
    default <T> Optional<T> getAbilityByClass(Class<T> clazz){
        return getAbilities().stream().filter(clazz::isInstance).map(clazz::cast).findFirst();
    }
}
