package game.gamelogic.combat;
import game.gameobjects.entities.Entity;

public interface OnMissed extends DefenderCombatModifier{
    public void doOnMissed(Entity self, Entity other, AttackInfo attackInfo);
}
