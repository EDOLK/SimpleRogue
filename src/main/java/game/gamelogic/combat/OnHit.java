package game.gamelogic.combat;
import game.gameobjects.entities.Entity;

public interface OnHit extends AttackerCombatModifier{
    public void doOnHit(Entity self, Entity other, AttackInfo attackInfo);
}
