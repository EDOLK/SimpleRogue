package game.gamelogic;

import java.util.List;

import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;

@Deprecated
public interface HasResistances{
    @Deprecated
    public List<Resistance> getResistances();
    @Deprecated
    default int applyResistances(int damage, DamageType damageType){
        for (Resistance resistance : getResistances()) {
            damage = resistance.calculateDamage(damage, damageType);
        }
        return Math.max(0, damage);
    }
}
