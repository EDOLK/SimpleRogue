package game.gamelogic;

import java.util.List;

import game.gamelogic.vulnerabilities.Vulnerability;
import game.gameobjects.DamageType;

@Deprecated
public interface HasVulnerabilities{
    @Deprecated
    public List<Vulnerability> getVulnerabilities();
    @Deprecated
    default int applyVulnerabilities(int damage, DamageType damageType){
        for (Vulnerability vulnerability : getVulnerabilities()) {
            damage = vulnerability.calculateDamage(damage, damageType);
        }
        return Math.max(0, damage);
    }
}
