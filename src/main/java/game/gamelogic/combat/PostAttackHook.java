package game.gamelogic.combat;

import game.gameobjects.AttackResult;
import game.gameobjects.entities.Entity;

@FunctionalInterface
public interface PostAttackHook {
    public void apply(AttackResult attackResult);

    public static Type generic(){
        return new Type();
    }

    public static Type generic(Entity target){
        return new Type(target);
    }

    public static Type onAttack(Entity target){
        return new Type(target, Condition.ON_ATTACK);
    }

    public static Type onAttacked(Entity target){
        return new Type(target, Condition.ON_ATTACKED);
    }

    public static Type onHit(Entity target){
        return new Type(target, Condition.ON_HIT);
    }

    public static Type onHitted(Entity target){
        return new Type(target, Condition.ON_HITTED);
    }

    public static Type onMiss(Entity target){
        return new Type(target, Condition.ON_MISS);
    }

    public static Type onMissed(Entity target){
        return new Type(target, Condition.ON_MISSED);
    }

    public static Type onCrit(Entity target){
        return new Type(target, Condition.ON_CRIT);
    }

    public static Type onCritted(Entity target){
        return new Type(target, Condition.ON_CRITTED);
    }

    public static Type onDeath(Entity target){
        return new Type(target, Condition.ON_DEATH);
    }

    public static Type onKill(Entity target){
        return new Type(target, Condition.ON_KILL);
    }

    public static class Type {
        public final Entity target;
        public final Condition condition;

        private Type(Entity target, Condition condition) {
            this.target = target;
            this.condition = condition;
        }

        private Type(Entity target){
            this(target, Condition.GENERIC);
        }

        private Type(){
            this(null, Condition.GENERIC);
        }

    }

    public static enum Condition{
        GENERIC, ON_ATTACK, ON_ATTACKED, ON_HIT, ON_HITTED, ON_MISS, ON_MISSED, ON_CRIT, ON_CRITTED, ON_KILL, ON_DEATH
    }
}
