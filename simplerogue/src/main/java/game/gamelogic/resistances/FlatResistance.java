package game.gamelogic.resistances;

import game.gamelogic.Levelable;
import game.gameobjects.DamageType;

public final class FlatResistance extends Resistance{

    private int flat;

    public int getFlat() {
        return flat;
    }

    public FlatResistance(DamageType type, int flat) {
        super(type);
        this.flat = flat;
    }

    public FlatResistance(DamageType type, Levelable levelable, int flat) {
        super(type, levelable);
        this.flat = flat;
    }

    @Override
    public int calculateDamage(int damage, DamageType damageType) {
        if (this.type == damageType){
            damage -= flat * getLevel();
        }
        return Math.max(0, damage);

    }

    @Override
    public String toString() {
        return getFlat() * getLevel() + " " + getType() + " damage";
    }
}