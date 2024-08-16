package game.gamelogic.combat;

import game.gameobjects.DamageType;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.weapons.Weapon;

public class AttackInfo{
    private Entity attacker;
    private Entity defender;
    private Weapon activeWeapon;
    private int defenderDogdge;
    private int baseRoll;
    private int modifiedRoll;
    private boolean crit;
    private boolean hit;
    private int damage;
    private DamageType damageType;
    private int damageDelt;
    public int getDamageDelt() {
        return damageDelt;
    }
    public void setDamageDelt(int damageDelt) {
        this.damageDelt = damageDelt;
    }
    public AttackInfo(Entity attacker, Entity defender, Weapon activeWeapon){
        this.attacker = attacker;
        this.defender = defender;
        this.activeWeapon = activeWeapon;
    }
    public boolean isHit() {
        return hit;
    }
    public void setHit(boolean hit) {
        this.hit = hit;
    }
    public Entity getAttacker() {
        return attacker;
    }
    public void setAttacker(Entity attacker) {
        this.attacker = attacker;
    }
    public Entity getDefender() {
        return defender;
    }
    public void setDefender(Entity defender) {
        this.defender = defender;
    }
    public Weapon getActiveWeapon() {
        return activeWeapon;
    }
    public void setActiveWeapon(Weapon activeWeapon) {
        this.activeWeapon = activeWeapon;
    }
    public int getDefenderDogdge() {
        return defenderDogdge;
    }
    public void setDefenderDogdge(int defenderDogdge) {
        this.defenderDogdge = defenderDogdge;
    }
    public int getBaseRoll() {
        return baseRoll;
    }
    public void setBaseRoll(int baseRoll) {
        this.baseRoll = baseRoll;
    }
    public int getModifiedRoll() {
        return modifiedRoll;
    }
    public void setModifiedRoll(int modifiedRoll) {
        this.modifiedRoll = modifiedRoll;
    }
    public boolean isCrit() {
        return crit;
    }
    public void setCrit(boolean crit) {
        this.crit = crit;
    }
    public int getDamage() {
        return damage;
    }
    public void setDamage(int damage) {
        this.damage = damage;
    }
    public DamageType getDamageType() {
        return damageType;
    }
    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }
}
