package game.gamelogic;

import game.gameobjects.DamageType;

public interface DamageModifier {
    public int calculateDamage(int damage, DamageType damageType);
}
