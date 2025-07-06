package game.gamelogic.combat;
import game.gameobjects.entities.Entity;

public interface OnDeath extends CombatModifier{
    public void doOnDeath(Entity self, Entity other, AttackInfo attackInfo);
}
