package game.gamelogic.combat;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
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
    private int dodge;

    private List<PostAttackHook> postAttackHooks = new ArrayList<>();
    private Map<PostAttackHook, PostAttackHook.Type> postAttackHookMap = new HashMap<>();
    private Deque<DodgeModifier> dodgeMods = new LinkedList<>();
    private Deque<AccuracyModifier> accuracyMods = new LinkedList<>();
    private Deque<DamageModifier> damageMods = new LinkedList<>();

    public void attachPostAttackHook(PostAttackHook hook){
        attachPostAttackHook(hook, PostAttackHook.generic());
    }

    public void attachPostAttackHook(PostAttackHook hook, PostAttackHook.Type type){
        postAttackHookMap.put(hook, type);
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

        dodge = 0;

        dodge += getDodge(defender);

        roll = App.randomNumber(1, 20);

        crit = roll == 20;

        modifiedRoll = roll;

        modifiedRoll += getAccuracy(attacker, attackerWeapon);
        
        damage = attackerWeapon.generateDamage();

        damage += Attribute.getAttribute(Attribute.STRENGTH, attacker);
        
        damage = crit ? damage * 2 : damage;

        damageType = attackerWeapon.getDamageType();

        hit = modifiedRoll >= dodge;
    }

    public AttackResult execute(){
        
        int damageDelt = 0;
        
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

        } else {

            if (attacker instanceof PlayerEntity){
                Display.log("You miss the " + defender.getName() + ".");
            } else if (defender instanceof PlayerEntity){
                Display.log("The " + attacker.getName() + " misses you.");
            } else {
                Display.log(attacker.getName() + " misses the " + defender.getName() + ".", defender.getSpace());
            }

        }

        AttackResult result = new AttackResult(hit, crit, damage, damageDelt, damageType, attacker, defender, weapon);
        for (PostAttackHook hook : postAttackHooks)
            hook.apply(result);

        for (Entry<PostAttackHook, PostAttackHook.Type> entry : postAttackHookMap.entrySet()) {
            switch (entry.getValue().condition) {
                case ON_ATTACK:
                    if (entry.getValue().target == result.attacker())
                        entry.getKey().apply(result);
                    break;
                case ON_ATTACKED:
                    if (entry.getValue().target == result.defender())
                        entry.getKey().apply(result);
                    break;
                case ON_CRIT:
                    if (entry.getValue().target == result.attacker() && result.crit())
                        entry.getKey().apply(result);
                    break;
                case ON_CRITTED:
                    if (entry.getValue().target == result.defender() && result.crit())
                        entry.getKey().apply(result);
                    break;
                case ON_DEATH:
                    if (entry.getValue().target == result.defender() && !result.defender().isAlive())
                        entry.getKey().apply(result);
                    break;
                case ON_HIT:
                    if (entry.getValue().target == result.attacker() && result.hit())
                        entry.getKey().apply(result);
                    break;
                case ON_HITTED:
                    if (entry.getValue().target == result.defender() && result.hit())
                        entry.getKey().apply(result);
                    break;
                case ON_KILL:
                    if (entry.getValue().target == result.attacker() && !result.defender().isAlive())
                        entry.getKey().apply(result);
                    break;
                case ON_MISS:
                    if (entry.getValue().target == result.attacker() && !result.hit())
                        entry.getKey().apply(result);
                    break;
                case ON_MISSED:
                    if (entry.getValue().target == result.defender() && !result.hit())
                        entry.getKey().apply(result);
                    break;
                default:
                    entry.getKey().apply(result);
                    break;

            }
        }

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
