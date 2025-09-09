package game.gamelogic.combat;
import game.gameobjects.entities.Entity;

public interface OnDeath extends DefenderCombatModifier{
    public void doOnDeath(Entity self, Entity other, AttackInfo attackInfo);
}
