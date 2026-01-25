package game.gamelogic.resistances;

import static java.lang.Integer.max;

import game.gamelogic.Levelable;
import game.gameobjects.DamageType;

public final class PercentageResistance extends Resistance{

    private Double percentage;
    private Double scale;

    public Double getPercentage() {
        double perc = percentage;
        for (int i = 0; i < getLevel(); i++) {
            perc += ((1 - perc) * scale);
        }
        return perc;
    }

    public PercentageResistance(DamageType type, Double percentage) {
        this(type, percentage, null, 0);
    }

    public PercentageResistance(DamageType type, Double percentage, Levelable levelable, double scale) {
        super(type, levelable);
        this.percentage = percentage;
        this.scale = scale;
    }


    @Override
    public int calculateDamage(int damage, DamageType damageType) {
        if (this.type == damageType){
            damage -= (int)(damage * getPercentage());
        }
        return max(0, damage);
    }

    @Override
    public String toString() {
        return (String.valueOf((int)(getPercentage() * 100)) + "% of " + this.getType() + " damage" );
    }
}
