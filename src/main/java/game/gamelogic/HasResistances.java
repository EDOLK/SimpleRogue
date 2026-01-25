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
            double percent = 0;
            for (Resistance resistance : hasResistances.getResistances()) {
                if (resistance.getType() != damageType)
                    continue;
                switch (resistance) {
                    case RangeResistance rangeResistance -> {
                        min += rangeResistance.getMinDamage();
                        max += rangeResistance.getMaxDamage();
                    }
                    case FlatResistance flatResistance -> {
                        min += flatResistance.getFlat();
                    }
                    case PercentageResistance percentageResistance -> {
                        percent += ((1 - percent) * percentageResistance.getPercentage());
                    }
                    default -> {

                    }
                }
            }
            percent *= 100;
            if (min != 0 || max != 0) {
                finalString += min;
                if (max != 0)
                    finalString += " - " + max;
            }
            if (percent != 0) {
                if (min != 0 || max != 0)
                    finalString += ", ";
                finalString += (int)percent + "%";
            }
            if (min != 0 || max != 0 || percent != 0)
                resistanceStrings.add(finalString);
        }
        return resistanceStrings;
    }
}
