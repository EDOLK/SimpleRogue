package game.gamelogic.combat;
import game.gameobjects.entities.Entity;

public interface OnCritted extends DefenderCombatModifier {
    public void doOnCritted(Entity self, Entity other, AttackInfo attackInfo);
}
