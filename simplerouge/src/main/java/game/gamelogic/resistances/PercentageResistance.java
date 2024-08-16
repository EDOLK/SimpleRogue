package game.gamelogic.resistances;

import static java.lang.Integer.max;

import game.gamelogic.Levelable;
import game.gameobjects.DamageType;

public final class PercentageResistance extends Resistance{

    private Double percentage;

    public Double getPercentage() {
        return percentage;
    }

    public PercentageResistance(DamageType type, Double percentage) {
        super(type);
        this.percentage = percentage;
    }

    public PercentageResistance(DamageType type, Levelable levelable, Double percentage) {
        super(type, levelable);
        this.percentage = percentage;
    }


    @Override
    public int calculateDamage(int damage, DamageType damageType) {
        if (this.type == damageType){
            for (int i = 0; i < getLevel(); i++) {
                damage -= (int)(damage * percentage);
            }
        }
        return max(0, damage);
    }

    @Override
    public String toString() {
        double t = 100.0;
        double p = 0.0;
        for (int i = 0; i < getLevel(); i++) {
            p += getPercentage() * t;
            t -= getPercentage() * t;
        }
        return (String.valueOf((int)(p)) + "% of " + this.getType() + " damage" );
    }
}
