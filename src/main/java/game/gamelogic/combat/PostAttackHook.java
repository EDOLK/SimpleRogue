package game.gamelogic.combat;

import game.gameobjects.AttackResult;

@FunctionalInterface
public interface PostAttackHook {
    public void apply(AttackResult attackResult);
}
