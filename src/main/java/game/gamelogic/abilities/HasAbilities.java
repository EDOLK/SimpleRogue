package game.gamelogic.abilities;

import java.util.List;

public interface HasAbilities {
    public List<Ability> getAbilities();
    public boolean addAbility(Ability ability);
    public boolean removeAbility(Ability ability);
}
