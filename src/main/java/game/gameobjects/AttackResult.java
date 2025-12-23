package game.gameobjects;

import game.gameobjects.entities.Entity;
import game.gameobjects.items.weapons.Weapon;

public record AttackResult(
    boolean hit,
    boolean crit,
    boolean sneakAttack,
    int damage,
    int damageDelt,
    DamageType damageType,
    Entity attacker,
    Entity defender,
    Weapon weapon
){
    public AttackResult() {
        this(false, false, false, 0, 0, null, null, null, null);
    }

    public AttackResult(Entity attacker, Entity defender) {
        this(false, false, false, 0, 0, null, attacker, defender, null);
    }
}
