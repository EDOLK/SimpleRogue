package game.gamelogic.combat;
import game.gameobjects.entities.Entity;

public interface OnAttack extends CombatModifier{
    public void doOnAttack(Entity self, Entity other, AttackInfo attackInfo);
}
