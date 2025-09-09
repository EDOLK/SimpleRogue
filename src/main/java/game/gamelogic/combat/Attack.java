package game.gamelogic.combat;

import java.util.List;
import java.util.Optional;

import game.App;
import game.CheckConditions;
import game.display.Display;
import game.gamelogic.Attribute;
import game.gamelogic.ModifiesAccuracy;
import game.gamelogic.ModifiesDodge;
import game.gameobjects.AttackResult;
import game.gameobjects.DamageType;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.weapons.Weapon;

public class Attack {

    private Entity attacker;
    private Entity defender;
    private Weapon activeWeapon;
    private boolean hit;
    private DamageType attackerDamageType;
    private int damage;
    private boolean crit;
    private int attackerRoll;
    private int attackerToHit;
    private AttackInfo attackInfo;
    private int defenderDodge;

    public Attack(Entity attacker, Entity defender, Weapon activeWeapon){
        this.attacker = attacker;
        this.defender = defender;
        this.activeWeapon = activeWeapon;

        attackInfo = new AttackInfo(attacker, defender, activeWeapon);
        defenderDodge = 0;

        defenderDodge += getDodge(defender);

        attackInfo.setDefenderDodge(defenderDodge);

        attackerRoll = App.randomNumber(1, 20);

        attackInfo.setBaseRoll(attackerRoll);

        crit = attackerRoll == 20;

        attackInfo.setCrit(crit);

        attackerToHit = attackerRoll;

        attackerToHit += getAccuracy(attacker, activeWeapon);

        attackInfo.setModifiedRoll(attackerToHit);
        
        damage = activeWeapon.generateDamage();

        damage += Attribute.getAttribute(Attribute.STRENGTH, attacker);
        
        damage = crit ? damage * 2 : damage;

        attackInfo.setDamage(damage);

        attackerDamageType = activeWeapon.getDamageType();
        
        attackInfo.setDamageType(attackerDamageType);

        hit = attackerToHit >= defenderDodge;

        attackInfo.setHit(hit);
    }

    public AttackResult execute(){
        
        List<AttackerCombatModifier> attackerCombatMods = getAttackerCombatMods(attacker);

        List<DefenderCombatModifier> defenderCombatMods = getDefenderCombatMods(defender);

        int damageDelt = 0;

        attackInfo.setDamageDelt(damageDelt);
        
        if (hit){

            if (crit) {
                if (attacker instanceof PlayerEntity){
                    Display.log("Critical hit!");
                } else if (defender instanceof PlayerEntity){
                    Display.log("The " + attacker.getName() + " scores a critical hit on you.");
                } else {
                    Display.log("The " + attacker.getName() + " scores a critical hit on the " + defender.getName() + ".", attacker.getSpace());
                }
            }

            damageDelt = defender.dealDamage(damage, attackerDamageType, attacker);

            attackInfo.setDamageDelt(damageDelt);

        } else {

            if (attacker instanceof PlayerEntity){
                Display.log("You miss the " + defender.getName() + ".");
            } else if (defender instanceof PlayerEntity){
                Display.log("The " + attacker.getName() + " misses you.");
            } else {
                Display.log(attacker.getName() + " misses the " + defender.getName() + ".", defender.getSpace());
            }

        }

        for (AttackerCombatModifier modifier : attackerCombatMods) {
            switch (modifier) {
                case OnAttack onAttack -> {
                    onAttack.doOnAttack(attacker, defender, attackInfo);
                }
                case OnHit onHit -> {
                    if (hit)
                        onHit.doOnHit(attacker, defender, attackInfo);
                }
                case OnMiss onMiss -> {
                    if (!hit)
                        onMiss.doOnMiss(attacker, defender, attackInfo);
                }
                case OnCrit onCrit -> {
                    if (crit)
                        onCrit.doOnCrit(attacker, defender, attackInfo);
                }
                case OnKill onKill -> {
                    if (!defender.isAlive())
                        onKill.doOnKill(attacker, defender, attackInfo);
                }
                default -> { }
            }
        }

        for (DefenderCombatModifier modifier : defenderCombatMods) {
            switch (modifier) {
                case OnAttacked onAttacked -> {
                    onAttacked.doOnAttacked(defender, attacker, attackInfo);
                }
                case OnHitted onHitted -> {
                    if (hit)
                        onHitted.doOnHitted(defender, attacker, attackInfo);
                }
                case OnMissed onMissed -> {
                    if (!hit)
                        onMissed.doOnMissed(defender, attacker, attackInfo);
                }
                case OnCritted onCritted -> {
                    if (crit)
                        onCritted.doOnCritted(defender, attacker, attackInfo);
                }
                case OnDeath onDeath -> {
                    if (!defender.isAlive())
                        onDeath.doOnDeath(defender, attacker, attackInfo);
                }
                default -> { }
            }
        }

        return new AttackResult(hit, crit, damageDelt, attackerDamageType, attacker, defender);
    }

    private static List<AttackerCombatModifier> getAttackerCombatMods(Entity entity){
        return App.recursiveCheck(entity, getCombatModConditions(), (obj) -> {
            if (obj instanceof AttackerCombatModifier cm){
                return Optional.of(cm);
            }
            return Optional.empty();
        });
    }
    private static List<DefenderCombatModifier> getDefenderCombatMods(Entity entity){
        return App.recursiveCheck(entity, getCombatModConditions(), (obj) -> {
            if (obj instanceof DefenderCombatModifier cm){
                return Optional.of(cm);
            }
            return Optional.empty();
        });
    }

    private static CheckConditions getCombatModConditions(){
        return CheckConditions.all()
            .withInventory(false);
    }

    private static int getDodge(Entity entity){
        int dodge = 0;
        for (ModifiesDodge modifiesDodge : App.recursiveCheck(entity, getDodgeConditions(), (obj) -> {
            if (obj instanceof ModifiesDodge md) {
                return Optional.of(md);
            }
            return Optional.empty();
        })) {
            dodge = modifiesDodge.modifyDodge(dodge);
        }
        return dodge;
    }

    private static CheckConditions getDodgeConditions(){
        return CheckConditions.all()
            .withInventory(false);
    }

    private static int getAccuracy(Entity entity, Weapon activeWeapon){

        int accuracy = 0;

        for (ModifiesAccuracy modifiesAccuracy : App.recursiveCheck(entity, getAccuracyConditions(), (obj) -> {
            if (obj instanceof ModifiesAccuracy ma) {
                return Optional.of(ma);
            }
            return Optional.empty();
        })){
            accuracy = modifiesAccuracy.modifyAccuracy(accuracy);
        };

        if (activeWeapon instanceof ModifiesAccuracy ma) {
            accuracy = ma.modifyAccuracy(accuracy);
        }

        return accuracy;

    }

    private static CheckConditions getAccuracyConditions(){
        return CheckConditions.all()
            .withInventory(false)
            .withArmedWeapons(false)
            .withUnarmedWeapon(false);
    }
}
