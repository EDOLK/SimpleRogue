package game.gamelogic;

import game.gameobjects.DamageType;

@FunctionalInterface
public interface DamageModifier {
    public int calculateDamage(int damage, DamageType damageType);
}
