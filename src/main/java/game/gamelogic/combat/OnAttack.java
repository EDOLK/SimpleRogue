package game.gamelogic.combat;
import game.gameobjects.entities.Entity;

public interface OnAttack extends AttackerCombatModifier{
    public void doOnAttack(Entity self, Entity other, AttackInfo attackInfo);
}
