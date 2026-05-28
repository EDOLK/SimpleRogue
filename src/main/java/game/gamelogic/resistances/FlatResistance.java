package game.gamelogic.resistances;

import game.gamelogic.Levelable;
import game.gameobjects.DamageType;

public final class FlatResistance extends Resistance{

    private int flat;
    private int scale;

    public int getFlat() {
        return flat + (scale * getLevel());
    }

    public FlatResistance(DamageType type, int flat) {
        this(type, flat, null, 0);
    }

    public FlatResistance(DamageType type, int flat, Levelable levelable, int scale) {
        super(type, levelable);
        this.flat = flat;
        this.scale = scale;
    }

    @Override
    public int calculateDamage(int damage, DamageType damageType) {
        if (this.type == damageType){
            damage -= getFlat();
        }
        return Math.max(0, damage);

    }

    @Override
    public String toString() {
        return getFlat() + " " + getType() + " damage";
    }
}
