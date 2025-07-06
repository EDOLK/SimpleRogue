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
            int percent = 0;
            for (Vulnerability vulnerability : hasVulnerabilities.getVulnerabilities()) {
                if (vulnerability.getType() != damageType){
                    continue;
                }
                if (vulnerability instanceof RangeVulnerability rangeVulnerability){
                    min += rangeVulnerability.getMinDamage() * rangeVulnerability.getLevel();
                    max += rangeVulnerability.getMaxDamage() * rangeVulnerability.getLevel();
                }
                if (vulnerability instanceof FlatVulnerability flatVulnerability){
                    min += flatVulnerability.getFlat() * flatVulnerability.getLevel();
                }
                if (vulnerability instanceof PercentageVulnerability percentageVulnerability){
                    double t = 100.0;
                    double p = 0.0;
                    for (int i = 0; i < percentageVulnerability.getLevel(); i++) {
                        p += percentageVulnerability.getPercentage() * t;
                        t -= percentageVulnerability.getPercentage() * t;
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
                vulnerabilityStrings.add(finalString);
            }
        }
        return vulnerabilityStrings;
    }
}
