package game.gamelogic.combat;
import game.gameobjects.entities.Entity;

public interface OnAttacked extends DefenderCombatModifier{
    public void doOnAttacked(Entity self, Entity other, AttackInfo attackInfo);
}
