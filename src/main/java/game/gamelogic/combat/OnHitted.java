package game.gamelogic.combat;
import game.gameobjects.entities.Entity;

public interface OnHitted extends DefenderCombatModifier{
    public void doOnHitted(Entity self, Entity other, AttackInfo attackInfo);
}
