package game.gamelogic.combat;
import game.gameobjects.entities.Entity;

public interface OnCrit extends CombatModifier{
    public void doOnCrit(Entity self, Entity other, AttackInfo attackInfo);
}
