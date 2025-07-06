package game.gamelogic;

import java.util.ArrayList;
import java.util.List;

import game.gamelogic.resistances.FlatResistance;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.RangeResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;

public interface HasResistances{
    public List<Resistance> getResistances();
    default int applyResistances(int damage, DamageType damageType){
        for (Resistance resistance : getResistances()) {
            damage = resistance.calculateDamage(damage, damageType);
        }
        return Math.max(0, damage);
    }
    public static List<String> getStrings(HasResistances hasResistances){
        List<String> resistanceStrings = new ArrayList<>();
        for (DamageType damageType : DamageType.values()) {
            String finalString = damageType.toString().toUpperCase() + ": ";
            int min = 0;
            int max = 0;
            int percent = 0;
            for (Resistance resistance : hasResistances.getResistances()) {
                if (resistance.getType() != damageType){
                    continue;
                }
                if (resistance instanceof RangeResistance rangeResistance){
                    min += rangeResistance.getMinDamage() * rangeResistance.getLevel();
                    max += rangeResistance.getMaxDamage() * rangeResistance.getLevel();
                }
                if (resistance instanceof FlatResistance flatResistance){
                    min += flatResistance.getFlat() * flatResistance.getLevel();
                }
                if (resistance instanceof PercentageResistance percentageResistance){
                    double t = 100.0;
                    double p = 0.0;
                    for (int i = 0; i < percentageResistance.getLevel(); i++) {
                        p += percentageResistance.getPercentage() * t;
                        t -= percentageResistance.getPercentage() * t;
                    }
                    percent += (int)p;
                }
                if (max != 0) {
                    finalString += min + " - " + max;
                } else if (min != 0){
                    finalString += min;
                }
                if (percent != 0){
                    if (min != 0 || max != 0) {
                        finalString += ", ";
                    }
                    finalString += percent + "%";
                }
            }
            if (min != 0 || max != 0 || percent != 0){
                resistanceStrings.add(finalString);
            }
        }
        return resistanceStrings;
    }
}
