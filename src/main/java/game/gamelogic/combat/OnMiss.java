package game.gamelogic.combat;
import game.gameobjects.entities.Entity;

public interface OnMiss extends AttackerCombatModifier{
    public void doOnMiss(Entity self, Entity other, AttackInfo attackInfo);
}
