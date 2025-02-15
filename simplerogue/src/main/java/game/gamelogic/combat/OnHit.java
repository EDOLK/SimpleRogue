package game.gamelogic.combat;
import game.gameobjects.entities.Entity;

public interface OnHit extends CombatModifier{
    public void doOnHit(Entity self, Entity other, AttackInfo attackInfo);
}
