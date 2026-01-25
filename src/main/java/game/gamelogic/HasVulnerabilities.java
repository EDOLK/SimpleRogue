package game.gamelogic;

import java.util.ArrayList;
import java.util.List;

import game.gamelogic.vulnerabilities.FlatVulnerability;
import game.gamelogic.vulnerabilities.PercentageVulnerability;
import game.gamelogic.vulnerabilities.RangeVulnerability;
import game.gamelogic.vulnerabilities.Vulnerability;
import game.gameobjects.DamageType;

public interface HasVulnerabilities{
    public List<Vulnerability> getVulnerabilities();
    default int applyVulnerabilities(int damage, DamageType damageType){
        for (Vulnerability vulnerability : getVulnerabilities()) {
            damage = vulnerability.calculateDamage(damage, damageType);
        }
        return Math.max(0, damage);
    }
    public static List<String> getStrings(HasVulnerabilities hasVulnerabilities){
        List<String> vulnerabilityStrings = new ArrayList<>();
        for (DamageType damageType : DamageType.values()) {
            String finalString = damageType.toString().toUpperCase() + ": ";
            int min = 0;
            int max = 0;
            double percent = 0;
            for (Vulnerability vulnerability : hasVulnerabilities.getVulnerabilities()) {
                if (vulnerability.getType() != damageType)
                    continue;
                switch (vulnerability) {
                    case RangeVulnerability rangeVulnerability -> {
                        min += rangeVulnerability.getMinDamage();
                        max += rangeVulnerability.getMaxDamage();
                    }
                    case FlatVulnerability flatVulnerability -> {
                        min += flatVulnerability.getFlat();
                    }
                    case PercentageVulnerability percentageVulnerability -> {
                        percent += ((1 - percent) * percentageVulnerability.getPercentage());
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
                vulnerabilityStrings.add(finalString);
        }
        return vulnerabilityStrings;

    }
}
