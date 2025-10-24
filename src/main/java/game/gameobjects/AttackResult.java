package game.gameobjects;

import game.gameobjects.entities.Entity;

public record AttackResult(
    boolean hit,
    boolean crit,
    int damage,
    int damageDelt,
    DamageType damageType,
    Entity attacker,
    Entity defender
){
    public AttackResult() {
        this(false, false, 0, 0, null, null, null);
    }
}
