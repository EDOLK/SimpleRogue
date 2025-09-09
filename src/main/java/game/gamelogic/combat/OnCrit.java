package game.gamelogic.combat;
import game.gameobjects.entities.Entity;

public interface OnCrit extends AttackerCombatModifier{
    public void doOnCrit(Entity self, Entity other, AttackInfo attackInfo);
}
