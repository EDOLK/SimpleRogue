package game.gamelogic.combat;

import game.gameobjects.entities.Entity;

public interface CombatModifier{
    public void activate(Entity self, Entity other, AttackInfo attackInfo);
}
