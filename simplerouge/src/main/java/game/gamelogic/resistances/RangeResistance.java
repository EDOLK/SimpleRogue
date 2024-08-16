package game.gamelogic.resistances;

import static game.App.randomNumber;

import game.gamelogic.Levelable;
import game.gameobjects.DamageType;

public final class RangeResistance extends Resistance {

    private int minDamage;
    private int maxDamage;

    public int getMinDamage() {
        return minDamage;
    }

    public void setMinDamage(int minDamage) {
        this.minDamage = minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public RangeResistance(DamageType type, int minDamage, int maxDamage) {
        super(type);
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }

    public RangeResistance(DamageType type, Levelable levelable, int minDamage, int maxDamage) {
        super(type, levelable);
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }
    
    private int generateDamage(){
        return randomNumber(minDamage, maxDamage);
    }


    @Override
    public int calculateDamage(int damage, DamageType damageType) {
        if (this.type == damageType){
            damage -= generateDamage() * getLevel();
        }
        return Math.max(0, damage);
    }

    @Override
    public String toString() {
        return minDamage * getLevel() + " - " + maxDamage * getLevel() + " " + getType() + " damage";
    }
}