package game.gamelogic.combat;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import game.App;
import game.CheckConditions;
import game.display.Display;
import game.gamelogic.Attribute;
import game.gamelogic.AccuracyModifier;
import game.gamelogic.DodgeModifier;
import game.gameobjects.AttackResult;
import game.gameobjects.DamageType;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.weapons.Weapon;
import game.gamelogic.DamageModifier;

public class Attack {

    private Entity attacker;
    private Entity defender;
    private Weapon weapon;
    private boolean hit;
    private DamageType damageType;
    private int damage;
    private int modifiedRoll;
    private boolean crit;
    private int roll;
    private AttackInfo attackInfo;
    private int dodge;

    private List<PostAttackHook> postAttackHooks = new ArrayList<>();
    private Deque<DodgeModifier> dodgeMods = new LinkedList<>();
    private Deque<AccuracyModifier> accuracyMods = new LinkedList<>();
    private Deque<DamageModifier> damageMods = new LinkedList<>();

    public void attachPostAttackHook(PostAttackHook hook){
        postAttackHooks.add(hook);
    }

    public void appendDodgeModifier(DodgeModifier mod){
        dodgeMods.addFirst(mod);
    }
    public void prependDodgeModifier(DodgeModifier mod){
        dodgeMods.addLast(mod);
    }
    public void appendAccuracyModifier(AccuracyModifier mod){
        accuracyMods.addFirst(mod);
    }
    public void prependAccuracyModifier(AccuracyModifier mod){
        accuracyMods.addLast(mod);
    }
    public void appendDamageModifier(DamageModifier mod){
        damageMods.addFirst(mod);
    }
    public void prependDamageModifier(DamageModifier mod){
        damageMods.addLast(mod);
    }

    public Entity getDefender() {
        return defender;
    }

    public Entity getAttacker() {
        return attacker;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public boolean isCrit() {
        return crit;
    }

    public Attack(Entity attacker, Entity defender, Weapon attackerWeapon){
        this.attacker = attacker;
        this.defender = defender;
        this.weapon = attackerWeapon;

        attackInfo = new AttackInfo(attacker, defender, attackerWeapon);
        dodge = 0;

        dodge += getDodge(defender);

        attackInfo.setDefenderDodge(dodge);

        roll = App.randomNumber(1, 20);

        attackInfo.setBaseRoll(roll);

        crit = roll == 20;

        attackInfo.setCrit(crit);

        modifiedRoll = roll;

        modifiedRoll += getAccuracy(attacker, attackerWeapon);

        attackInfo.setModifiedRoll(modifiedRoll);
        
        damage = attackerWeapon.generateDamage();

        damage += Attribute.getAttribute(Attribute.STRENGTH, attacker);
        
        damage = crit ? damage * 2 : damage;

        attackInfo.setDamage(damage);

        damageType = attackerWeapon.getDamageType();
        
        attackInfo.setDamageType(damageType);

        hit = modifiedRoll >= dodge;

        attackInfo.setHit(hit);
    }

    public AttackResult execute(){
        
        int damageDelt = 0;

        attackInfo.setDamageDelt(damageDelt);
        
        Stream.concat(getAttackModifiers(attacker).stream(), getAttackModifiers(defender).stream()).forEach(am -> am.modifyAttack(this));

        for (AccuracyModifier accuracyModifier : accuracyMods)
            modifiedRoll = accuracyModifier.modifyAccuracy(modifiedRoll);

        for (DodgeModifier dodgeModifier : dodgeMods)
            dodge = dodgeModifier.modifyDodge(dodge);

        for (DamageModifier damageModifier : damageMods)
            damage = damageModifier.calculateDamage(damage, damageType);

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

            damageDelt = defender.dealDamage(damage, damageType, attacker);

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

        AttackResult result = new AttackResult(hit, crit, damage, damageDelt, damageType, attacker, defender);
        for (PostAttackHook hook : postAttackHooks)
            hook.apply(result);
        return result;
    }

    private static List<AttackModifier> getAttackModifiers(Entity entity){
        return App.recursiveCheck(entity, getCombatModConditions(), (obj) -> {
            return obj instanceof AttackModifier attackModifier ? Optional.of(attackModifier) : Optional.empty();
        });
    }

    private static CheckConditions getCombatModConditions(){
        return CheckConditions.all()
            .withInventory(false);
    }

    private static int getDodge(Entity entity){
        int dodge = 0;
        for (DodgeModifier modifiesDodge : App.recursiveCheck(entity, getDodgeConditions(), (obj) -> {
            if (obj instanceof DodgeModifier md) {
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

        for (AccuracyModifier modifiesAccuracy : App.recursiveCheck(entity, getAccuracyConditions(), (obj) -> {
            if (obj instanceof AccuracyModifier ma) {
                return Optional.of(ma);
            }
            return Optional.empty();
        })){
            accuracy = modifiesAccuracy.modifyAccuracy(accuracy);
        };

        if (activeWeapon instanceof AccuracyModifier ma) {
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
