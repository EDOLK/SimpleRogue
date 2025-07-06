package game.gamelogic.combat;
import game.gameobjects.entities.Entity;

public interface OnKill extends CombatModifier{
    public void doOnKill(Entity self, Entity other, AttackInfo attackInfo);
}
