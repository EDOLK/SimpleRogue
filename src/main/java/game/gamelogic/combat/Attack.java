package game.gamelogic.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.Map.Entry;
import java.util.stream.Stream;

import game.App;
import game.CheckConditions;
import game.display.Display;
import game.gamelogic.AccuracyModifier;
import game.gamelogic.DodgeModifier;
import game.gameobjects.AttackResult;
import game.gameobjects.DamageType;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.weapons.Weapon;
import kotlin.Pair;
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

    private Map<PostAttackHook, PostAttackHook.Type> postAttackHookMap = new HashMap<>();
    private PriorityQueue<Pair<DodgeModifier, Integer>> dodgeModifierQueue = new PriorityQueue<>((p1,p2) -> p1.getSecond() - p2.getSecond());
    private PriorityQueue<Pair<AccuracyModifier, Integer>> accuracyModifierQueue = new PriorityQueue<>((p1,p2) -> p1.getSecond() - p2.getSecond());
    private PriorityQueue<Pair<DamageModifier, Integer>> damageModifierQueue = new PriorityQueue<>((p1,p2) -> p1.getSecond() - p2.getSecond());

    public static final int BASE_PRIORITY = 0;
    public static final int CRIT_PRIORITY = 10;

    public void attachPostAttackHook(PostAttackHook hook){
        attachPostAttackHook(hook, PostAttackHook.generic());
    }
    public void attachPostAttackHook(PostAttackHook hook, PostAttackHook.Type type){
        postAttackHookMap.put(hook, type);
    }

    public void attachDodgeModifier(DodgeModifier mod, int priority){
        dodgeModifierQueue.add(new Pair<DodgeModifier, Integer>(mod, priority));
    }
    public void attachAccuracyModifier(AccuracyModifier mod, int priority){
        accuracyModifierQueue.add(new Pair<AccuracyModifier, Integer>(mod, priority));
    }
    public void attachDamageModifier(DamageModifier mod, int priority){
        damageModifierQueue.add(new Pair<DamageModifier, Integer>(mod, priority));
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

        roll = App.randomNumber(1, 20);

        crit = roll == 20;

        modifiedRoll = roll;

        getDodge(defender);

        getAccuracy(attacker, attackerWeapon);
        
        attachDamageModifier((dmg,type) -> attackerWeapon.generateDamage(), BASE_PRIORITY);

        damageType = attackerWeapon.getDamageType();

        attachDamageModifier((dmg,type) -> crit ? dmg * 2 : dmg, CRIT_PRIORITY);
    }

    public AttackResult execute(){
        
        int damageDelt = 0;
        
        getAttackModifiers(attacker, defender, weapon).forEach(am -> am.modifyAttack(this));

        while (!accuracyModifierQueue.isEmpty()) {
            modifiedRoll = accuracyModifierQueue.poll().getFirst().modifyAccuracy(modifiedRoll);
        }

        while (!dodgeModifierQueue.isEmpty()) {
            dodge = dodgeModifierQueue.poll().getFirst().modifyDodge(dodge);
        }

        while (!damageModifierQueue.isEmpty()) {
            damage = damageModifierQueue.poll().getFirst().calculateDamage(damage, damageType);
        }

        hit = modifiedRoll >= dodge;

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

    private static Stream<AttackModifier> getAttackModifiers(Entity attacker, Entity defender, Weapon weapon){
        Function<Object, Optional<AttackModifier>> function = (obj) -> obj instanceof AttackModifier attackModifier ? Optional.of(attackModifier) : Optional.empty();
        return App.concatStreams(
            App.recursiveCheck(attacker, CheckConditions.all().withInventory(false).withArmedWeapons(false).withUnarmedWeapon(false), function).stream(),
            App.recursiveCheck(weapon, CheckConditions.all(), function).stream(),
            App.recursiveCheck(defender, CheckConditions.all().withInventory(false), function).stream()
        );
    }

    private void getDodge(Entity entity){
        App.recursiveCheck(
            entity,
            getDodgeConditions(),
            (obj) -> obj instanceof DodgeModifier md ? Optional.of(md) : Optional.empty()
        ).forEach(
            (d) -> attachDodgeModifier(d, BASE_PRIORITY)
        );
    }

    private static CheckConditions getDodgeConditions(){
        return CheckConditions.all()
            .withInventory(false);
    }

    private void getAccuracy(Entity entity, Weapon activeWeapon){
        App.recursiveCheck(
            entity,
            getAccuracyConditions(),
            (obj) -> obj instanceof AccuracyModifier ac ? Optional.of(ac) : Optional.empty()
        ).forEach(
            (a) -> attachAccuracyModifier(a, BASE_PRIORITY)
        );
        App.recursiveCheck(
            activeWeapon,
            getAccuracyConditions(),
            (obj) -> obj instanceof AccuracyModifier ac ? Optional.of(ac) : Optional.empty()
        ).forEach(
            (a) -> attachAccuracyModifier(a, BASE_PRIORITY)
        );
    }

    private static CheckConditions getAccuracyConditions(){
        return CheckConditions.all()
            .withInventory(false)
            .withArmedWeapons(false)
            .withUnarmedWeapon(false);
    }
}
