package game.gamelogic;

import game.gameobjects.DamageType;
import game.gameobjects.entities.Entity;

public interface Damageable {
    public void dealDamage(int damage, DamageType damageType, Entity attacker);
}
